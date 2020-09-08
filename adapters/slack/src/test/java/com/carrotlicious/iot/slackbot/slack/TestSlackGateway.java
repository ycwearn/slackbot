package com.carrotlicious.iot.slackbot.slack;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import com.carrotlicious.iot.slackbot.common.JsonHelper;
import com.carrotlicious.iot.slackbot.slack.dto.FileUploadRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

@ExtendWith({SpringExtension.class})
public class TestSlackGateway {

  private WireMockServer wireMockServer;

  private SlackGateway slackGateway;

  /**
   * Initiates the mock server for slack APIs.
   */
  @BeforeEach
  public void beforeEach() {
    wireMockServer = new WireMockServer();
    wireMockServer.start();
    slackGateway =
        new RestSlackGateway(WebClient.builder().baseUrl(wireMockServer.baseUrl()).build());
  }

  @AfterEach
  public void afterEach() {
    wireMockServer.stop();
  }

  @Test
  public void sendMessage_givenValidMessageContent_receivedHttp200WithOkEqualsTrueResponse() {
    final MessageResponse response = this.mockResponse();

    stubFor(
        post(urlEqualTo("/chat.postMessage"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                    .withBody(JsonHelper.safeWriteValueAsString(response, ""))));

    StepVerifier.create(slackGateway.sendMessage(this.mockMessageRequest()))
        .expectNext(response)
        .expectComplete()
        .verify();
  }

  @Test
  public void sendAttachment_givenValidAttachmentContent_receivedHttp200WithOkEqualsTrueResponse() {
    final MessageResponse response = this.mockResponse();

    stubFor(
        post(urlEqualTo("/files.upload"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                    .withBody(JsonHelper.safeWriteValueAsString(response, ""))));

    StepVerifier.create(slackGateway.sendAttachment(this.mockAttachmentRequest()))
        .expectNext(response)
        .expectComplete()
        .verify();
  }

  private MessageRequest mockMessageRequest() {
    return new MessageRequest.Builder().channel("CHANNEL_1").text("Message Plain.").build();
  }

  private FileUploadRequest mockAttachmentRequest() {
    return new FileUploadRequest.Builder()
        .filename("FILE_1")
        .base64("AAAABBBBCCCC")
        .channel("CHANNEL_1")
        .title("File 1")
        .build();
  }

  private MessageResponse mockResponse() {
    return new MessageResponse.Builder().channel("CHANNEL_1").ok(true).ts("123456789").build();
  }
}
