package com.carrotlicious.iot.slackbot.filewatcher;

import static com.carrotlicious.iot.slackbot.common.CollectionHelper.nullSafeCollection;
import static java.util.Comparator.comparing;

import com.carrotlicious.iot.slackbot.filewatcher.config.FileWatcherConfig;
import com.carrotlicious.iot.slackbot.filewatcher.dto.FileMetadata;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FileSystemDeviceFileRepository implements DeviceFileRepository {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileSystemDeviceFileRepository.class);

    private FileWatcherConfig fileWatcherConfig;
    private DirectoryWatcherFactory directoryWatcherFactory;
    private WatchService watchService;

    private ExecutorService executorService;

    private final Map<String, SortedSet<FileMetadata>> cacheLookup = new HashMap<>();

    private Set<DirectoryWatcher> directoryWatchers;

    /**
     * Constructs the {@link FileSystemDeviceFileRepository}.
     *
     * @param fileWatcherConfig       The configuration with file watcher details.
     * @param directoryWatcherFactory The factory class to construct directory watchers.
     * @param watchService            The watch service used to monitor directory and file changes.
     */
    @Autowired
    public FileSystemDeviceFileRepository(
            final FileWatcherConfig fileWatcherConfig,
            final DirectoryWatcherFactory directoryWatcherFactory,
            final WatchService watchService) {
        this.fileWatcherConfig = fileWatcherConfig;
        this.directoryWatcherFactory = directoryWatcherFactory;
        this.watchService = watchService;
    }

    /**
     * Creates list of directory watchers and startup the executor service.
     */
    @Override
    public void init() {
        this.directoryWatchers = Optional.ofNullable(this.fileWatcherConfig.getDeviceDirectories())
                .orElseGet(Collections::emptyMap)
                .values().stream()
                .peek(dir -> LOGGER.debug("Creating watcher for {}", dir))
                .map(dir -> this.directoryWatcherFactory
                        .createDirectoryWatcher(this.watchService, Paths.get(dir)))
                .collect(Collectors.toSet());

        this.startupWatchServiceListener(this.watchService);

    }

    /**
     * Performs resource cleanup for executor services, directory watchers and clear the lookup
     * cache.
     */
    @Override
    public void cleanup() {
        Optional.ofNullable(this.executorService).ifPresent(ExecutorService::shutdownNow);
        nullSafeCollection(directoryWatchers).stream().forEach(DirectoryWatcher::cleanup);
        this.cacheLookup.clear();
    }

    @Override
    public FileMetadata getLatestByDeviceId(final String deviceId) {

        if (fileWatcherConfig.getDeviceDirectories().containsKey(deviceId)) {
            return nullSafeCollection(this.cacheLookup.get(deviceId))
                    .stream()
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private void startupWatchServiceListener(final WatchService watchService) {
        this.executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
                    try {
                        WatchKey key;
                        LOGGER.info("Start listening to WatchService: {}", watchService.hashCode());
                        while ((key = watchService.take()) != null) {
                            LOGGER.trace("Processing watchkey: {}", key.hashCode());

                            DirectoryWatcher curWatcher = null;

                            for (DirectoryWatcher watcher : this.directoryWatchers) {
                                if (watcher.canHandle(key)) {
                                    curWatcher = watcher;
                                    break;
                                }
                            }

                            if (curWatcher == null) {
                                LOGGER.warn("Cannot find directory watcher for watchkey: {}", key.hashCode());
                                key.cancel();
                                continue;
                            }

                            for (WatchEvent<?> event : key.pollEvents()) {
                                final Optional<Path> eventPathOpt = curWatcher.resolvePath(event, key);

                                if (!eventPathOpt.isPresent()) {
                                    LOGGER.warn("Cannot resolved key and event. | key: {}, event: {}", key.hashCode(),
                                            event.context().toString());
                                    continue;
                                }

                                final Path eventPath = eventPathOpt.get();
                                if (Files.isDirectory(eventPath)) {
                                    LOGGER.debug("<{}> Event kind: {}. Directory affected: {}, parent: {}",
                                            key.hashCode(), event.kind(), eventPath.getFileName(), eventPath.getParent());
                                    curWatcher.onReceived(eventPath);
                                } else if (Files.isRegularFile(eventPath)) {
                                    LOGGER.debug("<{}> Event kind: {}. File affected: {}, parent: {}",
                                            key.hashCode(), event.kind(), eventPath.getFileName(), eventPath.getParent());
                                    this.cacheFile(eventPath);
                                } else {
                                    LOGGER.warn("{} is not a file or directory.", eventPath.getFileName());
                                }
                            }
                            key.reset();
                        }
                    } catch (final InterruptedException iex) {
                        LOGGER.info("Stop listening to WatchService: {}.", watchService.hashCode());
                    } catch (final Exception ex) {
                        LOGGER.error("Error in file watcher,", ex);
                    }
                }
        );
    }

    private void cacheFile(final Path filePath) {
        final String filename = filePath.getFileName().toString();
        if (!"mp4".equals(StringUtils.getFilenameExtension(filename))) {
            return;
        }

        final SortedSet<FileMetadata> queue = this.upsertQueue(filePath);

        if (queue != null) {
            try {
                final FileMetadata metadata = new FileMetadata.Builder()
                        .deviceFile(filePath)
                        .timestamp(this.getFileTimestamp(filePath))
                        .build();

                if (queue.size() >= this.fileWatcherConfig.getCacheSize()) {
                    final FileMetadata oldMeta = queue.last();
                    queue.remove(oldMeta);
                    LOGGER.debug("Removed old video file from cache: {} [{}]", oldMeta.getDeviceFile(),
                            oldMeta.getTimestamp());
                }
                queue.add(metadata);
            } catch (final DateTimeParseException e) {
                LOGGER.error("Ignore the give file as fail to parse the date time from it's path."
                        + " | filePath: {} | errorMessage: {}", filePath, e.getMessage());
            }

            if (LOGGER.isDebugEnabled()) {
                queue.stream().forEachOrdered(meta -> {
                    LOGGER.debug("IN QUEUE: {} [{}]", meta.getDeviceFile(), meta.getTimestamp());
                });
            }
        }
    }

    private SortedSet<FileMetadata> upsertQueue(final Path filePath) {
        return this.fileWatcherConfig.getDeviceDirectories().entrySet().stream()
                .peek(entry ->
                        LOGGER.trace("Try to match filePath to identify deviceId. | {}:{}, matchingPath: {}",
                                entry.getKey(), entry.getValue(), filePath))
                .filter(entry -> filePath.startsWith(entry.getValue()))
                .map(Map.Entry::getKey)
                .map(deviceId -> {
                    SortedSet<FileMetadata> deviceQueue = this.cacheLookup.get(deviceId);
                    if (deviceQueue == null) {
                        deviceQueue = Collections
                                .synchronizedSortedSet(
                                        new TreeSet<>(comparing(FileMetadata::getTimestamp).reversed()));
                        this.cacheLookup.put(deviceId, deviceQueue);
                        LOGGER.debug("Device[{}] queue is empty, created a new queue.", deviceId);
                    }
                    return deviceQueue;
                })
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime getFileTimestamp(final Path filePath) throws DateTimeParseException {
        String dateStr = StringUtils.stripFilenameExtension(filePath.getFileName().toString());
        for (int i = 2; i <= 3; i++) {
            dateStr = filePath.getName(filePath.getNameCount() - i) + dateStr;
        }
        LOGGER.debug("filePath: {}. dateStr: {}", filePath, dateStr);
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(ZoneId.of("GMT"));
        return ZonedDateTime.parse(dateStr, formatter)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
