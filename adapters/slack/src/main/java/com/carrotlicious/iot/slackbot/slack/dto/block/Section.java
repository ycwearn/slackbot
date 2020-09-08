package com.carrotlicious.iot.slackbot.slack.dto.block;

import com.carrotlicious.iot.slackbot.slack.dto.element.BaseElement;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("section")
public class Section extends BaseBlock {

  private BaseElement text;

  private Section(final Builder builder) {
    text = builder.text;
  }

  public BaseElement getText() {
    return text;
  }

  public static final class Builder {

    private BaseElement text;

    public Builder() {
    }

    public Builder text(final BaseElement val) {
      text = val;
      return this;
    }

    public Section build() {
      return new Section(this);
    }
  }
}
