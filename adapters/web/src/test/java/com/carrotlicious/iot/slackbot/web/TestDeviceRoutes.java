package com.carrotlicious.iot.slackbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.carrotlicious.iot.slackbot.application.port.in.SendLastestDeviceFileUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendLatestDeviceFileCommand;
import com.carrotlicious.iot.slackbot.web.dto.DeviceVideoRequestSchema;
import com.carrotlicious.iot.slackbot.web.mapper.SendLatestDeviceFileCommandMapper;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import reactor.core.publisher.Mono;

@ExtendWith({SpringExtension.class})
@SpringBootTest(
    classes = {DeviceRoutes.class, DeviceHandler.class, SendLatestDeviceFileCommandMapper.class})
public class TestDeviceRoutes {

  private static final String TEST_DEVICE_ID = "DEVICE_1";
  private static final String TEST_MESSAGE = "Dummy message.";

  private WebTestClient client;

  @Autowired private RouterFunction routes;

  @MockBean private SendLastestDeviceFileUseCase sendLastestDeviceFileUseCase;

  @BeforeEach
  public void beforeEach() {
    this.client =
        WebTestClient.bindToRouterFunction(routes).configureClient().baseUrl("/api/device").build();
  }

  @Test
  public void sendAttachment_givenDeviceIdAndMessage_mappedToCommandDeviceIdAndTitle() {
    final ArgumentCaptor<SendLatestDeviceFileCommand> argument =
        ArgumentCaptor.forClass(SendLatestDeviceFileCommand.class);

    doReturn(Mono.empty())
        .when(sendLastestDeviceFileUseCase)
        .sendLastestDeviceFile(argument.capture());

    this.client
        .post()
        .uri("/attachment")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(this.mockRequest()))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .isEmpty();

    final SendLatestDeviceFileCommand command = argument.getValue();

    assertThat(command).as("Check command not null.").isNotNull();

    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly.assertThat(command.getDeviceId()).as("Device Id").isEqualTo(TEST_DEVICE_ID);
      softly.assertThat(command.getTitle()).as("Title").isEqualTo(TEST_MESSAGE);
    }
  }

  @Test
  public void sendAttachment_givenDeviceIdIsNull_returnHttp400() {

    doReturn(Mono.error(new SendLastestDeviceFileUseCase.MissingDeviceIdException()))
        .when(sendLastestDeviceFileUseCase)
        .sendLastestDeviceFile(any(SendLatestDeviceFileCommand.class));

    this.client
        .post()
        .uri("/attachment")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(this.mockEmptyDeviceIdRequest()))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.error")
        .isNotEmpty()
        .jsonPath("$.error")
        .isEqualTo("Missing device id.");
  }

  @Test
  public void sendAttachment_givenDeviceFileNotFound_returnHttp500() {

    doReturn(
            Mono.error(
                new SendLastestDeviceFileUseCase.DeviceFileNotFoundException(TEST_DEVICE_ID)))
        .when(sendLastestDeviceFileUseCase)
        .sendLastestDeviceFile(any(SendLatestDeviceFileCommand.class));

    final String errorMessage =
        String.format("No file found for device with ID: %s", TEST_DEVICE_ID);

    this.client
        .post()
        .uri("/attachment")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(this.mockRequest()))
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.error")
        .isNotEmpty()
        .jsonPath("$.error")
        .isEqualTo(errorMessage);
  }

  private DeviceVideoRequestSchema mockRequest() {
    return new DeviceVideoRequestSchema.Builder()
        .deviceId(TEST_DEVICE_ID)
        .messagePlain(TEST_MESSAGE)
        .build();
  }

  private DeviceVideoRequestSchema mockEmptyDeviceIdRequest() {
    return new DeviceVideoRequestSchema.Builder().build();
  }
}
