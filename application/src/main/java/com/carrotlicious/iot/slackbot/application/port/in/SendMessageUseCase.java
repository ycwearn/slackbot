package com.carrotlicious.iot.slackbot.application.port.in;

import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageCommand;
import reactor.core.publisher.Mono;

public interface SendMessageUseCase {

  Mono<Void> sendMessage(SendMessageCommand command);

}
