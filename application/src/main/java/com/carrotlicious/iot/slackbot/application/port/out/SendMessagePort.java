package com.carrotlicious.iot.slackbot.application.port.out;

import com.carrotlicious.iot.slackbot.domain.Message;
import reactor.core.publisher.Mono;

public interface SendMessagePort {

  Mono<Void> sendMessage(Message message);

}
