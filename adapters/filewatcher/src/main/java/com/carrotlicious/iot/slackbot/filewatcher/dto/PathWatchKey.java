package com.carrotlicious.iot.slackbot.filewatcher.dto;

import java.nio.file.Path;
import java.nio.file.WatchKey;

public class PathWatchKey {

  private Path directory;
  private WatchKey watchKey;

  private PathWatchKey(final Builder builder) {
    directory = builder.directory;
    watchKey = builder.watchKey;
  }

  public Path getDirectory() {
    return directory;
  }

  public WatchKey getWatchKey() {
    return watchKey;
  }

  public static final class Builder {

    private Path directory;
    private WatchKey watchKey;

    public Builder() {
    }

    public Builder directory(final Path val) {
      directory = val;
      return this;
    }

    public Builder watchKey(final WatchKey val) {
      watchKey = val;
      return this;
    }

    public PathWatchKey build() {
      return new PathWatchKey(this);
    }
  }
}
