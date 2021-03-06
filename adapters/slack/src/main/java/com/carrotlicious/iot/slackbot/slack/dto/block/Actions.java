package com.carrotlicious.iot.slackbot.slack.dto.block;

import com.carrotlicious.iot.slackbot.slack.dto.element.BaseElement;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;

@JsonTypeName("actions")
public class Actions extends BaseBlock {

  private List<BaseElement> elements;

  private Actions(final Builder builder) {
    elements = builder.elements;
  }

  public List<BaseElement> getElements() {
    return elements;
  }


  public static final class Builder {

    private List<BaseElement> elements;

    public Builder() {
    }

    public Builder elements(final List<BaseElement> val) {
      elements = val;
      return this;
    }

    public Actions build() {
      return new Actions(this);
    }
  }
}
