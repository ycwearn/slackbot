package com.carrotlicious.iot.slackbot.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;

@JsonDeserialize(builder = DeviceVideoRequestSchema.Builder.class)
public class DeviceVideoRequestSchema implements Serializable {

  private static final long serialVersionUID = 1671781385699068007L;

  private String messagePlain;
  private String deviceId;

  private DeviceVideoRequestSchema(final Builder builder) {
    messagePlain = builder.messagePlain;
    deviceId = builder.deviceId;
  }

  public String getMessagePlain() {
    return messagePlain;
  }

  public String getDeviceId() {
    return deviceId;
  }

  @Override
  public String toString() {
    return "{\"DeviceVideoRequestSchema\":{"
        + "\"messagePlain\":\"" + messagePlain + "\""
        + ", \"deviceId\":\"" + deviceId + "\""
        + "}}";
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String messagePlain;
    private String deviceId;

    public Builder() {
    }

    public Builder messagePlain(final String val) {
      messagePlain = val;
      return this;
    }

    public Builder deviceId(final String val) {
      deviceId = val;
      return this;
    }

    public DeviceVideoRequestSchema build() {
      return new DeviceVideoRequestSchema(this);
    }
  }
}
