package com.carrotlicious.iot.slackbot.slack.persistence;

import com.carrotlicious.iot.slackbot.application.port.out.PersistMessagePort;
import com.carrotlicious.iot.slackbot.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PersistenceAdapter implements PersistMessagePort {

  private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAdapter.class);

  @Override
  public Mono<Message> saveMessage(final Message message) {
    LOGGER.debug("Persists message: {}", message);
    return Mono.just(message);
  }
}
