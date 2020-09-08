package com.carrotlicious.iot.slackbot.slack.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
public class FileUploadRequest implements Serializable {

  private static final long serialVersionUID = -4603190233285768126L;

  private String channel;
  private String title;
  private String filename;
  private String base64;

  private FileUploadRequest(final Builder builder) {
    channel = builder.channel;
    title = builder.title;
    filename = builder.filename;
    base64 = builder.base64;
  }

  public String getChannel() {
    return channel;
  }

  public String getTitle() {
    return title;
  }

  public String getFilename() {
    return filename;
  }

  public String getBase64() {
    return base64;
  }

  public static final class Builder {

    private String channel;
    private String title;
    private String filename;
    private String base64;

    public Builder() {
    }

    public Builder channel(final String val) {
      channel = val;
      return this;
    }

    public Builder title(final String val) {
      title = val;
      return this;
    }

    public Builder filename(final String val) {
      filename = val;
      return this;
    }

    public Builder base64(final String val) {
      base64 = val;
      return this;
    }

    public FileUploadRequest build() {
      return new FileUploadRequest(this);
    }
  }
}
