package com.carrotlicious.iot.slackbot.domain;

import com.carrotlicious.iot.slackbot.domain.Message.Builder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.List;

@JsonDeserialize(builder = Builder.class)
public class Message implements Serializable {

  private static final long serialVersionUID = 7356593736071098081L;

  private String id;
  private String messageMarkdown;
  private String messagePlain;
  private List<MessageAction> actions;

  private Message(Builder builder) {
    id = builder.id;
    messageMarkdown = builder.messageMarkdown;
    messagePlain = builder.messagePlain;
    actions = builder.actions;
  }

  public String getId() {
    return id;
  }

  public String getMessageMarkdown() {
    return messageMarkdown;
  }

  public String getMessagePlain() {
    return messagePlain;
  }

  public List<MessageAction> getActions() {
    return actions;
  }


  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String messageMarkdown;
    private String messagePlain;
    private List<MessageAction> actions;
    private String id;

    public Builder() {
    }

    public Builder messageMarkdown(String val) {
      messageMarkdown = val;
      return this;
    }

    public Builder messagePlain(String val) {
      messagePlain = val;
      return this;
    }

    public Builder actions(List<MessageAction> val) {
      actions = val;
      return this;
    }

    public Builder id(String val) {
      id = val;
      return this;
    }

    public Message build() {
      return new Message(this);
    }

  }
}
