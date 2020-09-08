package com.carrotlicious.iot.slackbot.application.port.in;

import com.carrotlicious.iot.slackbot.application.port.in.command.SendLatestDeviceFileCommand;
import com.carrotlicious.iot.slackbot.common.exception.BaseException;
import reactor.core.publisher.Mono;

public interface SendLastestDeviceFileUseCase {

  String ORIGIN = "APPLICATION";

  Mono<Void> sendLastestDeviceFile(SendLatestDeviceFileCommand command);


  final class MissingDeviceIdException extends BaseException {
    private static final long serialVersionUID = 1510207903445669934L;

    public MissingDeviceIdException() {
      super("Missing device id.", ORIGIN, null);
    }
  }

  final class DeviceFileNotFoundException extends BaseException {
    private static final long serialVersionUID = -2788284432573046078L;

    public DeviceFileNotFoundException(final String deviceId) {
      super(String.format("No file found for device with ID: %s", deviceId), ORIGIN, null);
    }
  }

}
