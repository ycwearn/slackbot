package com.carrotlicious.iot.slackbot.slack.dto.element;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonInclude(Include.NON_NULL)
@JsonTypeName("button")
public class ButtonElement extends BaseElement {

  private TextElement text;
  private String style;
  private String value;
  @JsonProperty("action_id")
  private String actionId;

  private ButtonElement(final Builder builder) {
    text = builder.text;
    style = builder.style;
    value = builder.value;
    actionId = builder.actionId;
  }

  public TextElement getText() {
    return text;
  }

  public String getStyle() {
    return style;
  }

  public String getValue() {
    return value;
  }

  public String getActionId() {
    return actionId;
  }

  public static final class Builder {

    private TextElement text;
    private String style;
    private String value;
    private String actionId;

    public Builder() {
    }

    public Builder text(final TextElement val) {
      text = val;
      return this;
    }

    public Builder style(final String val) {
      style = val;
      return this;
    }

    public Builder value(final String val) {
      value = val;
      return this;
    }

    public Builder actionId(final String val) {
      actionId = val;
      return this;
    }

    public ButtonElement build() {
      return new ButtonElement(this);
    }
  }
}
