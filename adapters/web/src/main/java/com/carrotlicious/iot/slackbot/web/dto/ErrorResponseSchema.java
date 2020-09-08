package com.carrotlicious.iot.slackbot.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;
import java.util.List;

@JsonDeserialize(builder = ErrorResponseSchema.Builder.class)
public class ErrorResponseSchema implements Serializable {

  private static final long serialVersionUID = -1826180375233502140L;

  private String error;
  private List<String> errorDetails;

  private ErrorResponseSchema(Builder builder) {
    setError(builder.error);
    setErrorDetails(builder.errorDetails);
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public List<String> getErrorDetails() {
    return errorDetails;
  }

  public void setErrorDetails(List<String> errorDetails) {
    this.errorDetails = errorDetails;
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private String error;
    private List<String> errorDetails;

    public Builder() {
    }

    public Builder error(String val) {
      error = val;
      return this;
    }

    public Builder errorDetails(List<String> val) {
      errorDetails = val;
      return this;
    }

    public ErrorResponseSchema build() {
      return new ErrorResponseSchema(this);
    }
  }
}
