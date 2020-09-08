package com.carrotlicious.iot.slackbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.carrotlicious.iot.slackbot.application.port.in.SendLastestDeviceFileUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.SendLastestDeviceFileUseCase.DeviceFileNotFoundException;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendLatestDeviceFileCommand;
import com.carrotlicious.iot.slackbot.application.port.out.FetchLatestDeviceFilePort;
import com.carrotlicious.iot.slackbot.application.port.out.SendDeviceFilePort;
import com.carrotlicious.iot.slackbot.application.service.SendLatestDeviceFileService;
import com.carrotlicious.iot.slackbot.common.exception.BaseException;
import com.carrotlicious.iot.slackbot.domain.DeviceFile;
import com.carrotlicious.iot.slackbot.domain.DeviceFileMessage;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SendLatestDeviceFileService.class)
@SpringBootTest
public class TestSendLatestDeviceFileService {

  private static final LocalDateTime TEST_DATETIME = LocalDateTime.of(2019, 8, 7, 10, 30);

  @MockBean
  private FetchLatestDeviceFilePort fetchLatestDeviceFilePort;

  @MockBean
  private SendDeviceFilePort sendDeviceFilePort;

  @Autowired
  private SendLastestDeviceFileUseCase sendLatestDeviceFileUseCase;

  /**
   * Sets the mock object for unit test.
   */
  @BeforeEach
  public void init() {
    // Mocking FetchLatestDeviceFilePort
    doReturn(this.mockDeviceFile())
        .when(fetchLatestDeviceFilePort).fetchLatest("mockDeviceId");
    doReturn(Mono.empty())
        .when(fetchLatestDeviceFilePort).fetchLatest("mockInvalidDeviceId");
  }

  @Test
  public void successSendDeviceFile() {
    final ArgumentCaptor<DeviceFileMessage> argument = ArgumentCaptor
        .forClass(DeviceFileMessage.class);
    final SendLatestDeviceFileCommand command = this.mockCommand();

    // Mocking success sendDeviceFilePort
    doReturn(Mono.empty()).when(sendDeviceFilePort).sendMessage(argument.capture());

    StepVerifier.create(this.sendLatestDeviceFileUseCase.sendLastestDeviceFile(command))
        .expectComplete().verify();

    verify(fetchLatestDeviceFilePort, times(1)).fetchLatest(command.getDeviceId());
    verify(sendDeviceFilePort, times(1)).sendMessage(any(DeviceFileMessage.class));

    final DeviceFileMessage message = argument.getValue();
    assertNotNull(message);
    assertEquals(command.getTitle(), message.getTitle());
    assertNotNull(message.getAttachment());
    assertEquals(command.getDeviceId(), message.getAttachment().getDeviceId());
    assertEquals(TEST_DATETIME, message.getAttachment().getTimestamp());
  }

  @Test
  public void failWithMissingDeviceId() {
    final SendLatestDeviceFileCommand command = new SendLatestDeviceFileCommand.Builder().build();

    StepVerifier.create(this.sendLatestDeviceFileUseCase.sendLastestDeviceFile(command))
        .expectError(SendLastestDeviceFileUseCase.MissingDeviceIdException.class)
        .verify();

    // Should not call the fetchLatestDeviceFilePort;
    verify(fetchLatestDeviceFilePort, never()).fetchLatest(anyString());
    // Should not call the fetchLatestDeviceFilePort;
    verify(sendDeviceFilePort, never()).sendMessage(any(DeviceFileMessage.class));
  }

  @Test
  public void failWithInvalidDeviceId() {
    final SendLatestDeviceFileCommand command = this.mockInvalidDeviceIdCommand();

    StepVerifier.create(this.sendLatestDeviceFileUseCase.sendLastestDeviceFile(command))
        .expectError(DeviceFileNotFoundException.class)
        .verify();

    // Should call the fetchLatestDeviceFilePort;
    verify(fetchLatestDeviceFilePort, times(1)).fetchLatest(anyString());
    // Should not call the fetchLatestDeviceFilePort;
    verify(sendDeviceFilePort, never()).sendMessage(any(DeviceFileMessage.class));
  }

  @Test
  public void failWithErrorSendingMessage() {
    final SendLatestDeviceFileCommand command = this.mockCommand();

    // Mocking error sendDeviceFilePort
    doReturn(Mono.error(new BaseException("Fail to send message.", "UNIT_TEST", null) {
    })).when(sendDeviceFilePort).sendMessage(any(DeviceFileMessage.class));

    StepVerifier.create(this.sendLatestDeviceFileUseCase.sendLastestDeviceFile(command))
        .expectError()
        .verify();

    // Should call the fetchLatestDeviceFilePort;
    verify(fetchLatestDeviceFilePort, times(1)).fetchLatest(anyString());
    // Should call the fetchLatestDeviceFilePort;
    verify(sendDeviceFilePort, times(1)).sendMessage(any(DeviceFileMessage.class));
  }

  // --- Test Support Methods ---

  private SendLatestDeviceFileCommand mockCommand() {
    return new SendLatestDeviceFileCommand.Builder()
        .deviceId("mockDeviceId")
        .title("Latest file attachment for mockDeviceId.")
        .build();
  }

  private SendLatestDeviceFileCommand mockInvalidDeviceIdCommand() {
    return new SendLatestDeviceFileCommand.Builder()
        .deviceId("mockInvalidDeviceId")
        .title("Latest file attachment for mockInvalidDeviceId.")
        .build();
  }

  private Mono<DeviceFile> mockDeviceFile() {
    return Mono.just(
        new DeviceFile.Builder()
            .deviceId("mockDeviceId")
            .timestamp(TEST_DATETIME)
            .file(null)
            .build());
  }


}
