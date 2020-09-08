package com.carrotlicious.iot.slackbot.slack;

import com.carrotlicious.iot.slackbot.common.JsonHelper;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import com.carrotlicious.iot.slackbot.slack.exception.SlackFailureException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public abstract class SlackAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(SlackAdapter.class);

  protected Mono<Void> handleResponse(final MessageResponse response) {
    if (response.isOk()) {
      LOGGER.debug("Successfully send slack message. | Response: \n{}",
          JsonHelper.safeWriteValueAsString(response, ""));
      return Mono.empty();
    } else {
      LOGGER.error("Fail to send slack message. | Response: \n{}",
          JsonHelper.safeWriteValueAsString(response, ""));

      final String error = response.getError();

      final List<String> errorDetails = Optional.of(response)
          .map(MessageResponse::getResponseMetadata)
          .map(m -> (List<String>) m.get("messages"))
          .orElse(Collections.EMPTY_LIST);

      return Mono.error(new SlackFailureException(error, errorDetails));
    }
  }
}
