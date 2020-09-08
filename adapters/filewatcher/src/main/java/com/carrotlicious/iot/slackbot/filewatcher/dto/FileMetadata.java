package com.carrotlicious.iot.slackbot.filewatcher.dto;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class FileMetadata implements Serializable {

  private static final long serialVersionUID = -5415399404864180903L;

  private LocalDateTime timestamp;
  private Path deviceFile;

  private FileMetadata(final Builder builder) {
    timestamp = builder.timestamp;
    deviceFile = builder.deviceFile;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public Path getDeviceFile() {
    return deviceFile;
  }

  public static final class Builder {

    private LocalDateTime timestamp;
    private Path deviceFile;

    public Builder() {
    }

    public Builder timestamp(final LocalDateTime val) {
      timestamp = val;
      return this;
    }

    public Builder deviceFile(final Path val) {
      deviceFile = val;
      return this;
    }

    public FileMetadata build() {
      return new FileMetadata(this);
    }
  }
}
