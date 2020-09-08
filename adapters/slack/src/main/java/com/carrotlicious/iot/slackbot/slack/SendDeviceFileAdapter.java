package com.carrotlicious.iot.slackbot.slack;

import com.carrotlicious.iot.slackbot.application.port.out.SendDeviceFilePort;
import com.carrotlicious.iot.slackbot.common.JsonHelper;
import com.carrotlicious.iot.slackbot.domain.DeviceFileMessage;
import com.carrotlicious.iot.slackbot.slack.config.SlackConfig;
import com.carrotlicious.iot.slackbot.slack.mapper.FileUploadRequestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SendDeviceFileAdapter extends SlackAdapter implements SendDeviceFilePort {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendDeviceFileAdapter.class);

  private SlackGateway slackGateway;
  private SlackConfig slackConfig;
  private FileUploadRequestMapper mapper;

  /**
   * Constructs a {@link SendDeviceFileAdapter} object.
   *
   * @param slackGateway The gateway to send slack messages.
   * @param slackConfig The configuration for slack channel.
   * @param mapper The mapper to convert domain into dto.
   */
  @Autowired
  public SendDeviceFileAdapter(
      final SlackGateway slackGateway,
      final SlackConfig slackConfig,
      final FileUploadRequestMapper mapper) {
    this.slackGateway = slackGateway;
    this.slackConfig = slackConfig;
    this.mapper = mapper;
  }

  @Override
  public Mono<Void> sendMessage(final DeviceFileMessage message) {
    return Mono.just(this.mapper.mapToFileUploadRequest(message, this.slackConfig.getChannel()))
        .doOnNext(
            req ->
                LOGGER.debug(
                    "Prepare file upload message: \n {}",
                    JsonHelper.safeWriteValueAsString(req, "")))
        .flatMap(slackGateway::sendAttachment)
        .flatMap(super::handleResponse);
  }
}
