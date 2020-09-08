package com.carrotlicious.iot.slackbot.slack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.carrotlicious.iot.slackbot.domain.Message;
import com.carrotlicious.iot.slackbot.domain.MessageAction;
import com.carrotlicious.iot.slackbot.slack.config.SlackConfig;
import com.carrotlicious.iot.slackbot.slack.dto.MessageRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import com.carrotlicious.iot.slackbot.slack.mapper.SlackMessageRequestMapper;
import java.util.Arrays;
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
    classes = {SlackConfig.class, SlackMessageRequestMapper.class, SendMessageAdapter.class})
public class TestSendMessageAdapter {

  private static final String TEST_MESSAGE_PLAIN = "Plain Message.";
  private static final String TEST_MESSAGE_MARKDOWN = "Markdown Message.";
  private static final String TEST_ACTION1_DISPLAY = "Action 1";
  private static final String TEST_ACTION1_VALUE = "Action_1";
  private static final String TEST_ACTION2_DISPLAY = "Action 2";
  private static final String TEST_ACTION2_VALUE = "Action_2";

  @MockBean private SlackGateway slackGateway;

  @Autowired private SlackConfig slackConfig;

  @Autowired private SendMessageAdapter sendMessageAdapter;

  @Test
  public void sendMessage_givenValidMessage_shouldReturnSuccessWithEmptyContent() {
    final ArgumentCaptor<MessageRequest> argument = ArgumentCaptor.forClass(MessageRequest.class);

    doReturn(Mono.just(this.mockSuccessResponse()))
        .when(slackGateway)
        .sendMessage(argument.capture());

    StepVerifier.create(sendMessageAdapter.sendMessage(this.mockMessage()))
        .expectComplete()
        .verify();

    final MessageRequest request = argument.getValue();
    assertThat(request).as("Message request to slack should not be null.").isNotNull();
    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly.assertThat(request.getChannel()).as("Channel").isEqualTo(slackConfig.getChannel());
      softly.assertThat(request.getText()).as("Text").isEqualTo(TEST_MESSAGE_PLAIN);
    }
  }

  @Test
  public void sendMessage_givenValidMessageWithoutAction_shouldReturnSuccessWithEmptyContent() {
    final ArgumentCaptor<MessageRequest> argument = ArgumentCaptor.forClass(MessageRequest.class);

    doReturn(Mono.just(this.mockSuccessResponse()))
        .when(slackGateway)
        .sendMessage(argument.capture());

    StepVerifier.create(sendMessageAdapter.sendMessage(this.mockMessageWithoutAction()))
        .expectComplete()
        .verify();

    final MessageRequest request = argument.getValue();
    assertThat(request).as("Message request to slack should not be null.").isNotNull();
    try (AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly.assertThat(request.getChannel()).as("Channel").isEqualTo(slackConfig.getChannel());
      softly.assertThat(request.getText()).as("Text").isEqualTo(TEST_MESSAGE_PLAIN);
    }
  }

  private Message mockMessage() {
    return new Message.Builder()
        .messagePlain(TEST_MESSAGE_PLAIN)
        .messageMarkdown(TEST_MESSAGE_MARKDOWN)
        .actions(
            Arrays.asList(
                new MessageAction.Builder()
                    .display(TEST_ACTION1_DISPLAY)
                    .value(TEST_ACTION1_VALUE)
                    .build(),
                new MessageAction.Builder()
                    .display(TEST_ACTION2_DISPLAY)
                    .value(TEST_ACTION2_VALUE)
                    .build()))
        .build();
  }

  private Message mockMessageWithoutAction() {
    return new Message.Builder()
        .messagePlain(TEST_MESSAGE_PLAIN)
        .messageMarkdown(TEST_MESSAGE_MARKDOWN)
        .build();
  }

  private MessageResponse mockSuccessResponse() {
    return new MessageResponse.Builder()
        .ok(true)
        .ts("123456789")
        .channel(this.slackConfig.getChannel())
        .build();
  }
}
