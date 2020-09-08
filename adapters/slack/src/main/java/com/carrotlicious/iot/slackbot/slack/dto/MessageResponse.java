package com.carrotlicious.iot.slackbot.slack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@JsonDeserialize(builder = MessageResponse.Builder.class)
@JsonInclude(Include.NON_NULL)
public class MessageResponse implements Serializable {

  private static final long serialVersionUID = 6541846774271228776L;

  private boolean ok;
  private String channel;
  private String ts;
  private String error;
  private String warning;
  private Map<String, Object> responseMetadata;

  private MessageResponse(final Builder builder) {
    ok = builder.ok;
    channel = builder.channel;
    ts = builder.ts;
    error = builder.error;
    warning = builder.warning;
    responseMetadata = builder.responseMetadata;
  }

  public boolean isOk() {
    return ok;
  }

  public String getChannel() {
    return channel;
  }

  public String getTs() {
    return ts;
  }

  public String getError() {
    return error;
  }

  public String getWarning() {
    return warning;
  }

  public Map<String, Object> getResponseMetadata() {
    return responseMetadata;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MessageResponse that = (MessageResponse) o;
    return ok == that.ok
        && channel.equals(that.channel)
        && ts.equals(that.ts)
        && Objects.equals(error, that.error)
        && Objects.equals(warning, that.warning)
        && Objects.equals(responseMetadata, that.responseMetadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ok, channel, ts, error, warning, responseMetadata);
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private boolean ok;
    private String channel;
    private String ts;
    private String error;
    private String warning;
    private Map<String, Object> responseMetadata;

    public Builder() {
    }

    public Builder ok(final boolean val) {
      ok = val;
      return this;
    }

    public Builder channel(final String val) {
      channel = val;
      return this;
    }

    public Builder ts(final String val) {
      ts = val;
      return this;
    }

    public Builder error(final String val) {
      error = val;
      return this;
    }

    public Builder warning(final String val) {
      warning = val;
      return this;
    }

    @JsonProperty("response_metadata")
    public Builder responseMetadata(final Map<String, Object> val) {
      responseMetadata = val;
      return this;
    }

    public MessageResponse build() {
      return new MessageResponse(this);
    }
  }
}
