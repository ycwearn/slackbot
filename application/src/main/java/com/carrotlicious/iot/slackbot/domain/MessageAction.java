package com.carrotlicious.iot.slackbot.domain;

import com.carrotlicious.iot.slackbot.domain.MessageAction.Builder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@JsonDeserialize(builder = Builder.class)
public class MessageAction implements Serializable {

  private static final long serialVersionUID = -1088271572611163330L;

  private String id;
  private String display;
  private String value;
  private Map<String, Object> metadatas;

  private MessageAction(Builder builder) {
    id = builder.id;
    display = builder.display;
    value = builder.value;
    metadatas = builder.metadatas;
  }

  public String getId() {
    return id;
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
    MessageAction that = (MessageAction) o;
    return Objects.equals(id, that.id)
        && Objects.equals(display, that.display)
        && Objects.equals(value, that.value)
        && Objects.equals(metadatas, that.metadatas);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, display, value, metadatas);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MessageAction.class.getSimpleName() + "[", "]")
        .add("id='" + id + "'")
        .add("display='" + display + "'")
        .add("value='" + value + "'")
        .add("metadatas=" + metadatas)
        .toString();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String id;
    private String display;
    private String value;
    private Map<String, Object> metadatas;

    public Builder() {
      // Empty constructor
    }

    public Builder id(String val) {
      id = val;
      return this;
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

    public MessageAction build() {
      return new MessageAction(this);
    }
  }
}
