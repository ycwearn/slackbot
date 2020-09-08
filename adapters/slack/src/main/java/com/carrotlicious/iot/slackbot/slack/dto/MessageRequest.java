package com.carrotlicious.iot.slackbot.slack.dto;

import com.carrotlicious.iot.slackbot.slack.dto.block.BaseBlock;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.List;

@JsonDeserialize(builder = MessageRequest.Builder.class)
@JsonInclude(Include.NON_NULL)
public class MessageRequest implements Serializable {

  private static final long serialVersionUID = -3134593947077123297L;

  private String channel;
  private String text;
  private List<BaseBlock> blocks;

  private MessageRequest(final Builder builder) {
    channel = builder.channel;
    text = builder.text;
    blocks = builder.blocks;
  }

  public String getChannel() {
    return channel;
  }

  public String getText() {
    return text;
  }

  public List<BaseBlock> getBlocks() {
    return blocks;
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String channel;
    private String text;
    private List<BaseBlock> blocks;

    public Builder() {
    }

    public Builder channel(final String val) {
      channel = val;
      return this;
    }

    public Builder text(final String val) {
      text = val;
      return this;
    }

    public Builder blocks(final List<BaseBlock> val) {
      blocks = val;
      return this;
    }

    public MessageRequest build() {
      return new MessageRequest(this);
    }
  }
}
