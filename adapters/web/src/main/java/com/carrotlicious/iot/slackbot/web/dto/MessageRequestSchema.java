package com.carrotlicious.iot.slackbot.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.List;

@JsonDeserialize(builder = MessageRequestSchema.Builder.class)
public class MessageRequestSchema implements Serializable {

  private static final long serialVersionUID = -996630560727321254L;

  private String messageMarkdown;
  private String messagePlain;
  private List<ActionSchema> actions;

  private MessageRequestSchema(Builder builder) {
    messageMarkdown = builder.messageMarkdown;
    messagePlain = builder.messagePlain;
    actions = builder.actions;
  }

  public String getMessageMarkdown() {
    return messageMarkdown;
  }

  public String getMessagePlain() {
    return messagePlain;
  }

  public List<ActionSchema> getActions() {
    return actions;
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String messageMarkdown;
    private String messagePlain;
    private List<ActionSchema> actions;

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

    public Builder actions(List<ActionSchema> val) {
      actions = val;
      return this;
    }

    public MessageRequestSchema build() {
      return new MessageRequestSchema(this);
    }
  }
}
