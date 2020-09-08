package com.carrotlicious.iot.slackbot.slack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.carrotlicious.iot.slackbot.domain.DeviceFile;
import com.carrotlicious.iot.slackbot.domain.DeviceFileMessage;
import com.carrotlicious.iot.slackbot.slack.config.SlackConfig;
import com.carrotlicious.iot.slackbot.slack.dto.FileUploadRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import com.carrotlicious.iot.slackbot.slack.mapper.FileUploadRequestMapper;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@EnableAutoConfiguration
@ExtendWith({SpringExtension.class})
@SpringBootTest(
    classes = {SlackConfig.class, FileUploadRequestMapper.class, SendDeviceFileAdapter.class})
public class TestSendDeviceFileAdapter {

  private static final String TEST_FILE_TITLE = "Test Title";
  private static final String TEST_DEVICE_ID = "Device_123";
  private static final Path TEST_FILE_PATH = Paths.get("src", "test", "resources", "dummy.txt");
  private static final Path TEST_INVALID_FILE_PATH =
      Paths.get("src", "test", "resources", "dummy1.txt");
  private static final LocalDateTime TEST_DATE_TIME = LocalDateTime.now();
  private static final String DEFAULT_TITLE = String.format("Attachment for %s", TEST_DEVICE_ID);

  @MockBean private SlackGateway slackGateway;

  @Autowired private SlackConfig slackConfig;

  @Autowired private SendDeviceFileAdapter sendDeviceFileAdapter;

  @Test
  public void sendMessage_givenValidMessage_returnSuccessWithEmptyContent() {
    final ArgumentCaptor<FileUploadRequest> argument =
        ArgumentCaptor.forClass(FileUploadRequest.class);

    doReturn(Mono.just(this.mockSuccessResponse()))
        .when(slackGateway)
        .sendAttachment(argument.capture());

    StepVerifier.create(sendDeviceFileAdapter.sendMessage(this.mockMessage()))
        .expectComplete()
        .verify();

    final FileUploadRequest request = argument.getValue();
    assertThat(request).as("Message request to slack should not be null.").isNotNull();
    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly.assertThat(request.getChannel()).as("Channel").isEqualTo(slackConfig.getChannel());
      softly.assertThat(request.getTitle()).as("Title").isEqualTo(TEST_FILE_TITLE);
      softly.assertThat(request.getFilename()).as("Filename").isEqualTo("dummy.txt");
      softly.assertThat(request.getBase64()).as("Base64").isNotBlank();
    }
  }

  @Test
  public void sendMessage_givenMessageWithoutTitle_shouldGenerateMessageTitleWithDeviceId() {
    final ArgumentCaptor<FileUploadRequest> argument =
        ArgumentCaptor.forClass(FileUploadRequest.class);

    doReturn(Mono.just(this.mockSuccessResponse()))
        .when(slackGateway)
        .sendAttachment(argument.capture());

    StepVerifier.create(sendDeviceFileAdapter.sendMessage(this.mockMessageWithoutTitle()))
        .expectComplete()
        .verify();

    final FileUploadRequest request = argument.getValue();
    assertThat(request).as("Message request to slack should not be null.").isNotNull();
    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly.assertThat(request.getChannel()).as("Channel").isEqualTo(slackConfig.getChannel());
      softly.assertThat(request.getTitle()).as("Title").isEqualTo(DEFAULT_TITLE);
      softly.assertThat(request.getFilename()).as("Filename").isEqualTo("dummy.txt");
      softly.assertThat(request.getBase64()).as("Base64").isNotBlank();
    }
  }

  @Test
  public void sendMessage_givenMessageWithoutValidFile_shouldGenerateEmptyContent() {
    final ArgumentCaptor<FileUploadRequest> argument =
        ArgumentCaptor.forClass(FileUploadRequest.class);

    doReturn(Mono.just(this.mockSuccessResponse()))
        .when(slackGateway)
        .sendAttachment(argument.capture());

    StepVerifier.create(sendDeviceFileAdapter.sendMessage(this.mockMessageWithoutValidFile()))
        .expectComplete()
        .verify();

    final FileUploadRequest request = argument.getValue();
    assertThat(request).as("Message request to slack should not be null.").isNotNull();
    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly.assertThat(request.getChannel()).as("Channel").isEqualTo(slackConfig.getChannel());
      softly.assertThat(request.getTitle()).as("Title").isEqualTo(TEST_FILE_TITLE);
      softly.assertThat(request.getFilename()).as("Filename").isEqualTo("dummy1.txt");
      softly.assertThat(request.getBase64()).as("Base64").isBlank();
    }
  }

  private DeviceFileMessage mockMessage() {
    final DeviceFile deviceFile =
        new DeviceFile.Builder()
            .deviceId(TEST_DEVICE_ID)
            .file(TEST_FILE_PATH)
            .timestamp(TEST_DATE_TIME)
            .build();

    return new DeviceFileMessage.Builder().title(TEST_FILE_TITLE).attachment(deviceFile).build();
  }

  private DeviceFileMessage mockMessageWithoutTitle() {
    final DeviceFile deviceFile =
        new DeviceFile.Builder()
            .deviceId(TEST_DEVICE_ID)
            .file(TEST_FILE_PATH)
            .timestamp(TEST_DATE_TIME)
            .build();

    return new DeviceFileMessage.Builder().attachment(deviceFile).build();
  }

  private DeviceFileMessage mockMessageWithoutValidFile() {
    final DeviceFile deviceFile =
        new DeviceFile.Builder()
            .deviceId(TEST_DEVICE_ID)
            .file(TEST_INVALID_FILE_PATH)
            .timestamp(TEST_DATE_TIME)
            .build();

    return new DeviceFileMessage.Builder().title(TEST_FILE_TITLE).attachment(deviceFile).build();
  }

  private MessageResponse mockSuccessResponse() {
    return new MessageResponse.Builder()
        .ok(true)
        .ts("123456789")
        .channel(this.slackConfig.getChannel())
        .build();
  }
}
