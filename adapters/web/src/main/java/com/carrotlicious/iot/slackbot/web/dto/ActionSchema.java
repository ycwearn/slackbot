package com.carrotlicious.iot.slackbot.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.Map;

@JsonDeserialize(builder = ActionSchema.Builder.class)
public class ActionSchema implements Serializable {

  private static final long serialVersionUID = -2002696684357577961L;

  private String display;
  private String value;
  private Map<String, Object> metadatas;

  private ActionSchema(Builder builder) {
    display = builder.display;
    value = builder.value;
    metadatas = builder.metadatas;
  }

  public String getDisplay() {
    return display;
  }

  public String getValue() {
    return value;
  }

  public Map<String, Object> getMetadatas() {
    return metadatas;
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String display;
    private String value;
    private Map<String, Object> metadatas;

    public Builder() {
    }

    public Builder display(String val) {
      display = val;
      return this;
    }

    public Builder value(String val) {
      value = val;
      return this;
    }

    public Builder metadatas(Map<String, Object> val) {
      metadatas = val;
      return this;
    }

    public ActionSchema build() {
      return new ActionSchema(this);
    }
  }
}
