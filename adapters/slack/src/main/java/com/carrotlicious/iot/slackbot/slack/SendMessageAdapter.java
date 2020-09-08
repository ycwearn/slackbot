package com.carrotlicious.iot.slackbot.slack;

import com.carrotlicious.iot.slackbot.application.port.out.SendMessagePort;
import com.carrotlicious.iot.slackbot.common.JsonHelper;
import com.carrotlicious.iot.slackbot.domain.Message;
import com.carrotlicious.iot.slackbot.slack.config.SlackConfig;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import com.carrotlicious.iot.slackbot.slack.exception.SlackFailureException;
import com.carrotlicious.iot.slackbot.slack.mapper.SlackMessageRequestMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SendMessageAdapter extends SlackAdapter implements SendMessagePort {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageAdapter.class);

  private final SlackGateway slackGateway;
  private final SlackMessageRequestMapper mapper;
  private final SlackConfig slackConfig;

  /**
   * Constructs a {@link SendMessageAdapter} object.
   * @param slackGateway The gateway to send slack messages.
   * @param slackConfig The configuration for slack channel.
   * @param mapper The mapper to convert domain into dto.
   */
  @Autowired
  public SendMessageAdapter(final SlackGateway slackGateway,
      final SlackMessageRequestMapper mapper, final SlackConfig slackConfig) {
    this.slackGateway = slackGateway;
    this.mapper = mapper;
    this.slackConfig = slackConfig;
  }

  @Override
  public Mono<Void> sendMessage(final Message message) {
    return Mono.just(mapper.mapToMessageRequest(message, this.slackConfig.getChannel()))
        .doOnSuccess(
            req ->
                LOGGER.debug(
                    "Prepared slack message: \n{}", JsonHelper.safeWriteValueAsString(req, "")))
        .flatMap(slackGateway::sendMessage)
        .flatMap(super::handleResponse);
  }

}
