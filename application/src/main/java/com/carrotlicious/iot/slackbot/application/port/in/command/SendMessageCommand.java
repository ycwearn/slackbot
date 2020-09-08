package com.carrotlicious.iot.slackbot.application.port.in.command;

import java.io.Serializable;
import java.util.List;

public class SendMessageCommand implements Serializable {

  private static final long serialVersionUID = 397976006861285231L;

  private String messageMarkdown;
  private String messagePlain;
  private List<SendMessageAction> actions;

  private SendMessageCommand(Builder builder) {
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

  public List<SendMessageAction> getActions() {
    return actions;
  }

  public static final class Builder {

    private String messageMarkdown;
    private String messagePlain;
    private List<SendMessageAction> actions;

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

    public Builder actions(List<SendMessageAction> val) {
      actions = val;
      return this;
    }

    public SendMessageCommand build() {
      return new SendMessageCommand(this);
    }
  }
}
