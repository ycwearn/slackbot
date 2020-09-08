package com.carrotlicious.iot.slackbot.filewatcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.util.Comparator.comparing;

import com.carrotlicious.iot.slackbot.common.FilesHelper;
import com.carrotlicious.iot.slackbot.filewatcher.dto.PathWatchKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceDirectoryWatcher implements DirectoryWatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceDirectoryWatcher.class);

  private final WatchService watchService;

  private final Deque<PathWatchKey> watchKeys = new ConcurrentLinkedDeque();

  public DeviceDirectoryWatcher(final WatchService watchService) {
    this.watchService = watchService;
  }

  @Override
  public void init(final Path rootDir) {
    this.recursiveWatchDirectory(rootDir, this.watchService, this.watchKeys);
  }

  @Override
  public void onReceived(final Path dirPath) {
    final Deque<PathWatchKey> keys = this.watchKeys;
    final boolean monitored = this.isDirectoryMonitored(dirPath, keys);

    if (!monitored) {
      PathWatchKey curKey;
      while ((curKey = keys.pop()) != null) {
        final Path curPath = curKey.getDirectory();
        if (FilesHelper.safeIsSameFile(dirPath.getParent(), curPath)) {
          // New sub directory
          LOGGER.debug("Found new sub-directory, parentDir: {}, newSubDir: {}", curPath, dirPath);
          keys.addFirst(curKey);
          this.recursiveWatchDirectory(dirPath, this.watchService, keys);
          break;
        } else if (FilesHelper.safeIsSameFile(dirPath.getParent(), curPath.getParent())) {
          //Same parent, going to replace
          //TODO: Should check the directory to determine if it is the latest
          LOGGER.debug("Found sibling directory, oldDir: {}, newDir: {}", curPath, dirPath);
          curKey.getWatchKey().cancel();
          this.recursiveWatchDirectory(dirPath, this.watchService, keys);
          break;
        }
        curKey.getWatchKey().cancel();
      }
    } else {
      LOGGER.trace("Directory {} already in monitored stack. No action taken.", dirPath);
    }
  }

  @Override
  public boolean canHandle(final WatchKey watchKey) {
    return this.watchKeys.stream()
        .filter(pwk -> pwk.getWatchKey().equals(watchKey))
        .map(PathWatchKey::getDirectory)
        .findAny()
        .isPresent();
  }

  @Override
  public Optional<Path> resolvePath(final WatchEvent watchEvent, final WatchKey watchKey) {
    return this.watchKeys.stream()
        .filter(pwk -> pwk.getWatchKey().equals(watchKey))
        .map(PathWatchKey::getDirectory)
        .findAny()
        .map(Path::toString)
        .map(rootPath -> Paths.get(rootPath, watchEvent.context().toString()));
  }

  @Override
  public void cleanup() {
    this.watchKeys.stream().forEach(wkp -> {
      LOGGER.debug("Cleanup watchkey for {}", wkp.getDirectory());
      wkp.getWatchKey().cancel();
    });
    this.watchKeys.clear();
  }

  protected void recursiveWatchDirectory(final Path directory, final WatchService watchService,
      final Deque<PathWatchKey> watchKeys) {
    try {
      final WatchKey key = directory.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
      watchKeys.push(new PathWatchKey.Builder().directory(directory).watchKey(key).build());
      key.reset();
      LOGGER.info("Watching directory: {} with key [{}]", directory, key.hashCode());
      try (Stream<Path> subDirectories = Files.list(directory)) {
        subDirectories.filter(Files::isDirectory)
            .max(comparing(Path::getFileName))
            .ifPresent(latestDir ->
                this.recursiveWatchDirectory(latestDir, watchService, watchKeys));
      }
    } catch (final IOException e) {
      LOGGER.error("Fail to register watch service for directory: {}", directory.getFileName(), e);
    }
  }

  private boolean isDirectoryMonitored(final Path dirPath, final Deque<PathWatchKey> keys) {
    return keys.stream().anyMatch(pwk -> FilesHelper.safeIsSameFile(dirPath, pwk.getDirectory()));
  }
}
