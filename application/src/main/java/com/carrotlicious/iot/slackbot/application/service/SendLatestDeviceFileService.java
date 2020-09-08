package com.carrotlicious.iot.slackbot.application.service;

import com.carrotlicious.iot.slackbot.application.port.in.SendLastestDeviceFileUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendLatestDeviceFileCommand;
import com.carrotlicious.iot.slackbot.application.port.out.FetchLatestDeviceFilePort;
import com.carrotlicious.iot.slackbot.application.port.out.SendDeviceFilePort;
import com.carrotlicious.iot.slackbot.domain.DeviceFile;
import com.carrotlicious.iot.slackbot.domain.DeviceFileMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
public class SendLatestDeviceFileService implements SendLastestDeviceFileUseCase {

  private static final Logger LOGGER = LoggerFactory.getLogger(SendLatestDeviceFileService.class);

  private final FetchLatestDeviceFilePort fetchLatestDeviceFilePort;
  private final SendDeviceFilePort sendDeviceFilePort;

  @Autowired(required = false)
  public SendLatestDeviceFileService(
      final FetchLatestDeviceFilePort fetchLatestDeviceFilePort,
      final SendDeviceFilePort sendDeviceFilePort) {
    this.fetchLatestDeviceFilePort = fetchLatestDeviceFilePort;
    this.sendDeviceFilePort = sendDeviceFilePort;
  }

  @Override
  public Mono<Void> sendLastestDeviceFile(final SendLatestDeviceFileCommand command) {
    return Mono.just(command)
        .doOnNext(cmd -> LOGGER.debug("Received SendLatestDeviceFileCommand: {}", cmd))
        .flatMap(this::validateDeviceId)
        .flatMap(cmd -> this.fetchLatestDeviceFilePort.fetchLatest(cmd.getDeviceId()))
        .switchIfEmpty(Mono.error(new DeviceFileNotFoundException(command.getDeviceId())))
        .map(file -> this.mapToDeviceFileMessage(command, file))
        .doOnSuccess(message -> LOGGER.info("Going to send message: {}", message))
        .flatMap(this.sendDeviceFilePort::sendMessage)
        .doOnError(e ->
            LOGGER.error("Fail to send device attachment. | deviceId: {} | errorMessage: {}",
                command.getDeviceId(), e.getMessage(), e));
  }

  private Mono<SendLatestDeviceFileCommand> validateDeviceId(
      final SendLatestDeviceFileCommand command) {
    if (StringUtils.isEmpty(command.getDeviceId())) {
      return Mono.error(new MissingDeviceIdException());
    } else {
      return Mono.just(command);
    }
  }

  private DeviceFileMessage mapToDeviceFileMessage(final SendLatestDeviceFileCommand command,
      final DeviceFile file) {
    return new DeviceFileMessage.Builder()
        .title(command.getTitle())
        .attachment(file)
        .build();
  }

}
