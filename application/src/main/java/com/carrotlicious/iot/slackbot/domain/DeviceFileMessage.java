package com.carrotlicious.iot.slackbot.domain;

import java.io.Serializable;

public class DeviceFileMessage implements Serializable {

  private static final long serialVersionUID = -5797846698964264857L;

  private String title;
  private DeviceFile attachment;

  private DeviceFileMessage(final Builder builder) {
    title = builder.title;
    attachment = builder.attachment;
  }

  public String getTitle() {
    return title;
  }

  public DeviceFile getAttachment() {
    return attachment;
  }

  @Override
  public String toString() {
    return "{\"DeviceFileMessage\":{"
        + "\"title\":\"" + title + "\""
        + ", \"attachment\":" + attachment
        + "}}";
  }

  public static final class Builder {

    private String title;
    private DeviceFile attachment;

    public Builder() {
    }

    public Builder title(final String val) {
      title = val;
      return this;
    }

    public Builder attachment(final DeviceFile val) {
      attachment = val;
      return this;
    }

    public DeviceFileMessage build() {
      return new DeviceFileMessage(this);
    }
  }
}
