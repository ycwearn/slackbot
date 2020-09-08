package com.carrotlicious.iot.slackbot.filewatcher;

import java.nio.file.Path;
import java.nio.file.WatchService;
import org.springframework.stereotype.Component;

@Component
public class DeviceDirectoryWatcherFactory implements DirectoryWatcherFactory {

  @Override
  public DirectoryWatcher createDirectoryWatcher(final WatchService watchService,
      final Path rootDir) {
    final DirectoryWatcher watcher = new DeviceDirectoryWatcher(watchService);
    watcher.init(rootDir);
    return watcher;
  }
}
