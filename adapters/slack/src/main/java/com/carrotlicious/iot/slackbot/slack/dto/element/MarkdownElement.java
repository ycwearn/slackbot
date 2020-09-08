package com.carrotlicious.iot.slackbot.slack.dto.element;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(Include.NON_NULL)
@JsonTypeName("mrkdwn")
public class MarkdownElement extends BaseElement {

  private String text;

  private MarkdownElement(final Builder builder) {
    text = builder.text;
  }

  public String getText() {
    return text;
  }

  public static final class Builder {

    private String text;

    public Builder() {
    }

    public Builder text(final String val) {
      text = val;
      return this;
    }

    public MarkdownElement build() {
      return new MarkdownElement(this);
    }
  }
}
