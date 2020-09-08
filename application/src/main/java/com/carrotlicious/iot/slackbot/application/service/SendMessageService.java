package com.carrotlicious.iot.slackbot.application.service;

import com.carrotlicious.iot.slackbot.application.port.in.SendMessageUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageAction;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageCommand;
import com.carrotlicious.iot.slackbot.application.port.out.PersistMessagePort;
import com.carrotlicious.iot.slackbot.application.port.out.SendMessagePort;
import com.carrotlicious.iot.slackbot.common.CollectionHelper;
import com.carrotlicious.iot.slackbot.domain.Message;
import com.carrotlicious.iot.slackbot.domain.MessageAction;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SendMessageService implements SendMessageUseCase {

  private PersistMessagePort persistMessagePort;
  private SendMessagePort sendMessagePort;

  @Autowired(required = false)
  public SendMessageService(final PersistMessagePort persistMessagePort,
      final SendMessagePort sendMessagePort) {
    this.persistMessagePort = persistMessagePort;
    this.sendMessagePort = sendMessagePort;
  }

  @Override
  public Mono<Void> sendMessage(SendMessageCommand command) {
    return Mono.just(this.mapCommandtoMessage(command))
        .flatMap(this.persistMessagePort::saveMessage)
        .flatMap(this.sendMessagePort::sendMessage);
  }

  protected Message mapCommandtoMessage(SendMessageCommand command) {
    return
        new Message.Builder()
            .messageMarkdown(command.getMessageMarkdown())
            .messagePlain(command.getMessagePlain())
            .actions(mapCommandActionsToActions(command.getActions()))
            .build();
  }

  protected List<MessageAction> mapCommandActionsToActions(
      final List<SendMessageAction> sendMessageActions) {
    return CollectionHelper
        .nullSafeList(sendMessageActions)
        .stream().map(action ->
            new MessageAction.Builder()
                .display(action.getDisplay())
                .value(action.getValue())
                .metadatas(action.getMetadatas())
                .build()
        ).collect(Collectors.toList());
  }


}
