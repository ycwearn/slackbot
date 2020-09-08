package com.carrotlicious.iot.slackbot.slack.mapper;

import com.carrotlicious.iot.slackbot.domain.DeviceFileMessage;
import com.carrotlicious.iot.slackbot.slack.dto.FileUploadRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

@Component
public class FileUploadRequestMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadRequestMapper.class);

  private static final String DEFAULT_TITLE = "Attachment for %s";

  /**
   * Constructs the dto from domain object.
   *
   * @param message The file upload message payload.
   * @param channel The slack channel.
   * @return The file upload dto to send slack file upload request.
   */
  public FileUploadRequest mapToFileUploadRequest(final DeviceFileMessage message,
      final String channel) {
    return new FileUploadRequest.Builder()
        .channel(channel)
        .title(this.constructTitle(message))
        .filename(message.getAttachment().getFile().getFileName().toString())
        .base64(this.safeFileToBase64(message.getAttachment().getFile()))
        .build();
  }

  private String constructTitle(final DeviceFileMessage message) {
    if (StringUtils.isEmpty(message.getTitle())) {
      return String.format(DEFAULT_TITLE, message.getAttachment().getDeviceId());
    }
    return message.getTitle();
  }

  private String safeFileToBase64(Path filePath) {
    try {
      return Base64Utils.encodeToString(Files.readAllBytes(filePath));
    } catch (IOException e) {
      LOGGER.error("Fail to convert file to base64. | file: {} | errorMessage: {}", filePath,
          e.getMessage());
    }
    return "";
  }

}
