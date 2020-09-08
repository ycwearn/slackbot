package com.carrotlicious.iot.slackbot.domain;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class DeviceFile implements Serializable {

  private static final long serialVersionUID = 5412830237755248259L;

  private String deviceId;
  private LocalDateTime timestamp;
  private Path file;

  private DeviceFile(final Builder builder) {
    deviceId = builder.deviceId;
    timestamp = builder.timestamp;
    file = builder.file;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public Path getFile() {
    return file;
  }

  @Override
  public String toString() {
    return "{\"DeviceFile\":{"
        + "\"deviceId\":\"" + deviceId + "\""
        + ", \"timestamp\":" + timestamp
        + ", \"file\":" + file
        + "}}";
  }

  public static final class Builder {

    private String deviceId;
    private LocalDateTime timestamp;
    private Path file;

    public Builder() {
    }

    public Builder deviceId(final String val) {
      deviceId = val;
      return this;
    }

    public Builder timestamp(final LocalDateTime val) {
      timestamp = val;
      return this;
    }

    public Builder file(final Path val) {
      file = val;
      return this;
    }

    public DeviceFile build() {
      return new DeviceFile(this);
    }
  }
}
