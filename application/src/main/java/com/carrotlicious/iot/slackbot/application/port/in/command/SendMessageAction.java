package com.carrotlicious.iot.slackbot.application.port.in.command;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class SendMessageAction implements Serializable {

  private static final long serialVersionUID = 4638111003570801730L;

  private String display;
  private String value;
  private Map<String, Object> metadatas;

  private SendMessageAction(Builder builder) {
    display = builder.display;
    value = builder.value;
    metadatas = builder.metadatas;
  }

  public String getDisplay() {
    return display;
  }

  public String getValue() {
    return value;
  }

  public Map<String, Object> getMetadatas() {
    return metadatas;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SendMessageAction that = (SendMessageAction) o;
    return display.equals(that.display)
        && value.equals(that.value)
        && Objects.equals(metadatas, that.metadatas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(display, value, metadatas);
  }

  public static final class Builder {

    private String display;
    private String value;
    private Map<String, Object> metadatas;

    public Builder() {
    }

    public Builder display(String val) {
      display = val;
      return this;
    }

    public Builder value(String val) {
      value = val;
      return this;
    }

    public Builder metadatas(Map<String, Object> val) {
      metadatas = val;
      return this;
    }

    public SendMessageAction build() {
      return new SendMessageAction(this);
    }
  }
}
