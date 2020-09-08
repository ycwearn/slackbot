package com.carrotlicious.iot.slackbot.filewatcher;

import java.nio.file.Path;
import java.nio.file.WatchService;

@FunctionalInterface
public interface DirectoryWatcherFactory {

  DirectoryWatcher createDirectoryWatcher(WatchService watchService, Path rootDir);

}
