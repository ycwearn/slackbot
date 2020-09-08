package com.carrotlicious.iot.slackbot.slack.dto.element;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(Include.NON_NULL)
@JsonTypeName("plain_text")
public class TextElement extends BaseElement {

  private String text;
  private boolean emoji = false;

  private TextElement(final Builder builder) {
    text = builder.text;
    emoji = builder.emoji;
  }

  public String getText() {
    return text;
  }

  public boolean isEmoji() {
    return emoji;
  }

  public static final class Builder {

    private String text;
    private boolean emoji;

    public Builder() {
    }

    public Builder text(final String val) {
      text = val;
      return this;
    }

    public Builder emoji(final boolean val) {
      emoji = val;
      return this;
    }

    public TextElement build() {
      return new TextElement(this);
    }
  }
}
