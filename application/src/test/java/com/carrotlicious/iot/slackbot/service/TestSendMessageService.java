package com.carrotlicious.iot.slackbot.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.carrotlicious.iot.slackbot.application.port.in.SendMessageUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageAction;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageCommand;
import com.carrotlicious.iot.slackbot.application.port.out.PersistMessagePort;
import com.carrotlicious.iot.slackbot.application.port.out.SendMessagePort;
import com.carrotlicious.iot.slackbot.application.service.SendMessageService;
import com.carrotlicious.iot.slackbot.domain.Message;
import com.carrotlicious.iot.slackbot.domain.MessageAction;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.assertj.core.api.AutoCloseableSoftAssertions;
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
@ContextConfiguration(classes = SendMessageService.class)
@SpringBootTest
public class TestSendMessageService {

  private static final String TEST_ACTION1_DISPLAY = "Action 1";
  private static final String TEST_ACTION1_VALUE = "Action_1";

  @MockBean private PersistMessagePort persistMessagePort;

  @MockBean private SendMessagePort sendMessagePort;

  @Autowired private SendMessageUseCase sendMessageUseCase;

  @Test
  public void successSendMessage() {
    final ArgumentCaptor<Message> argumentMessage = ArgumentCaptor.forClass(Message.class);

    final SendMessageCommand command = this.mockCommand();
    final Message mockMessage = this.mockMessage(command);

    doReturn(Mono.just(mockMessage))
        .when(persistMessagePort)
        .saveMessage(argumentMessage.capture());
    doReturn(Mono.empty()).when(sendMessagePort).sendMessage(any(Message.class));

    // Execution
    StepVerifier.create(this.sendMessageUseCase.sendMessage(command)).expectComplete().verify();

    verify(persistMessagePort, times(1)).saveMessage(any(Message.class));
    verify(sendMessagePort, times(1)).sendMessage(any(Message.class));

    final Message message = argumentMessage.getValue();
    assertThat(message).as("Message parameter should not be null.").isNotNull();
    try (final AutoCloseableSoftAssertions softly = new AutoCloseableSoftAssertions()) {
      softly
          .assertThat(message.getMessagePlain())
          .as("Plain Message")
          .isEqualTo(command.getMessagePlain());
      softly
          .assertThat(message.getMessageMarkdown())
          .as("Markdown Message")
          .isEqualTo(command.getMessageMarkdown());
      softly
          .assertThat(message.getActions())
          .as("Action List")
          .hasSize(1)
          .containsExactly(mockMessage.getActions().get(0));
    }
  }

  private SendMessageCommand mockCommand() {
    return new SendMessageCommand.Builder()
        .messageMarkdown("Test *Message*")
        .messagePlain("Test Message")
        .actions(
            Arrays.asList(
                new SendMessageAction.Builder()
                    .display(TEST_ACTION1_DISPLAY)
                    .value(TEST_ACTION1_VALUE)
                    .build()))
        .build();
  }

  private Message mockMessage(final SendMessageCommand command) {
    return new Message.Builder()
        .messageMarkdown(command.getMessageMarkdown())
        .messagePlain(command.getMessagePlain())
        .actions(
            command.getActions().stream()
                .map(
                    sma ->
                        new MessageAction.Builder()
                            .display(sma.getDisplay())
                            .value(sma.getValue())
                            .metadatas(sma.getMetadatas())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }
}
