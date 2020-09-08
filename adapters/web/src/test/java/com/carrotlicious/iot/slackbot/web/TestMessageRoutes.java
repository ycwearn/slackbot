package com.carrotlicious.iot.slackbot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.carrotlicious.iot.slackbot.application.port.in.SendMessageUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageAction;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageCommand;
import com.carrotlicious.iot.slackbot.web.dto.ActionSchema;
import com.carrotlicious.iot.slackbot.web.dto.MessageRequestSchema;
import com.carrotlicious.iot.slackbot.web.mapper.SendMessageCommandMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    classes = {MessageRoutes.class, MessageHandler.class, SendMessageCommandMapper.class})
public class TestMessageRoutes {

  private static final String TEST_PLAIN_MESSAGE = "Plain Message";
  private static final String TEST_MARKDOWN_MESSAGE = "Markdown Message";
  private static final String TEST_ACTION1_DISPLAY = "ACTION 1";
  private static final String TEST_ACTION1_VALUE = "ACTION_1";
  private static final String TEST_ACTION2_DISPLAY = "ACTION 2";
  private static final String TEST_ACTION2_VALUE = "ACTION_2";
  private static final String TEST_METADATA_CALLBACK_KEY = "CALLBACK";
  private static final String TEST_METADATA_CALLBACK_VALUE = "http://mocking.com";
  private static final String TEST_RUNTIME_ERROR_MESSAGE = "Error sending.";

  private WebTestClient client;

  @Autowired private RouterFunction routes;

  @MockBean private SendMessageUseCase sendMessageUseCase;

  @BeforeEach
  public void beforeEach() {
    this.client =
        WebTestClient.bindToRouterFunction(routes).configureClient().baseUrl("/api").build();
  }

  @Test
  public void sendMessage_givenMessageMarkdownAndActions_mappedToSendMessageCommand() {
    final ArgumentCaptor<SendMessageCommand> argument =
        ArgumentCaptor.forClass(SendMessageCommand.class);

    doReturn(Mono.empty()).when(sendMessageUseCase).sendMessage(argument.capture());

    this.client
        .post()
        .uri("/message")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(this.mockRequest()))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .isEmpty();

    final SendMessageCommand command = argument.getValue();

    assertThat(command).as("Check command not null.").isNotNull();

    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      final Map<String, Object> metadatas = new HashMap<>();
      metadatas.put(TEST_METADATA_CALLBACK_KEY, TEST_METADATA_CALLBACK_VALUE);

      softly
          .assertThat(command.getMessagePlain())
          .as("Plain Message")
          .isEqualTo(TEST_PLAIN_MESSAGE);
      softly
          .assertThat(command.getMessageMarkdown())
          .as("Markdown Message")
          .isEqualTo(TEST_MARKDOWN_MESSAGE);
      softly
          .assertThat(command.getActions())
          .as("Message Actions")
          .hasSize(2)
          .containsExactly(
              new SendMessageAction.Builder()
                  .display(TEST_ACTION1_DISPLAY)
                  .value(TEST_ACTION1_VALUE)
                  .metadatas(metadatas)
                  .build(),
              new SendMessageAction.Builder()
                  .display(TEST_ACTION2_DISPLAY)
                  .value(TEST_ACTION2_VALUE)
                  .metadatas(metadatas)
                  .build());
    }
  }

  @Test
  public void sendMessage_givenIssueSending_returnHttp500() {

    doReturn(Mono.error(new RuntimeException(TEST_RUNTIME_ERROR_MESSAGE)))
        .when(sendMessageUseCase)
        .sendMessage(any(SendMessageCommand.class));

    this.client
        .post()
        .uri("/message")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(this.mockRequest()))
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.error")
        .isEqualTo(TEST_RUNTIME_ERROR_MESSAGE);
  }

  private MessageRequestSchema mockRequest() {
    final Map<String, Object> metadatas = new HashMap<>();
    metadatas.put(TEST_METADATA_CALLBACK_KEY, TEST_METADATA_CALLBACK_VALUE);

    final ActionSchema ac1 =
        new ActionSchema.Builder()
            .display(TEST_ACTION1_DISPLAY)
            .value(TEST_ACTION1_VALUE)
            .metadatas(metadatas)
            .build();
    final ActionSchema ac2 =
        new ActionSchema.Builder()
            .display(TEST_ACTION2_DISPLAY)
            .value(TEST_ACTION2_VALUE)
            .metadatas(metadatas)
            .build();

    return new MessageRequestSchema.Builder()
        .messagePlain(TEST_PLAIN_MESSAGE)
        .messageMarkdown(TEST_MARKDOWN_MESSAGE)
        .actions(Arrays.asList(ac1, ac2))
        .build();
  }
}
