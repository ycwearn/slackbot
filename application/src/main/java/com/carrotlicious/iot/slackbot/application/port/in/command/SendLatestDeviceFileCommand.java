package com.carrotlicious.iot.slackbot.application.port.in.command;

import java.io.Serializable;
import java.util.StringJoiner;

public class SendLatestDeviceFileCommand implements Serializable {

  private static final long serialVersionUID = 7553155078308309467L;

  private String title;
  private String deviceId;

  private SendLatestDeviceFileCommand(final Builder builder) {
    title = builder.title;
    deviceId = builder.deviceId;
  }

  public String getTitle() {
    return title;
  }

  public String getDeviceId() {
    return deviceId;
  }

  @Override
  public String toString() {
    return "{\"SendLatestDeviceFileCommand\":{"
        + "\"title\":\"" + title + "\""
        + ", \"deviceId\":\"" + deviceId + "\""
        + "}}";
  }

  public static final class Builder {

    private String title;
    private String deviceId;

    public Builder() {
    }

    public Builder title(final String val) {
      title = val;
      return this;
    }

    public Builder deviceId(final String val) {
      deviceId = val;
      return this;
    }

    public SendLatestDeviceFileCommand build() {
      return new SendLatestDeviceFileCommand(this);
    }
  }
}
