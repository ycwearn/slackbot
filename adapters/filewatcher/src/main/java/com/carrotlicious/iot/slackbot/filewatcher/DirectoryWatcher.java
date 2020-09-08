package com.carrotlicious.iot.slackbot.filewatcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.Optional;

public interface DirectoryWatcher {

  void init(Path path);

  void onReceived(Path dirPath);

  boolean canHandle(WatchKey watchKey);

  Optional<Path> resolvePath(WatchEvent watchEvent, WatchKey watchKey);

  void cleanup();

}
