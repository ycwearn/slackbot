package com.carrotlicious.iot.slackbot.web.mapper;

import static java.util.Optional.ofNullable;

import com.carrotlicious.iot.slackbot.application.port.in.command.SendLatestDeviceFileCommand;
import com.carrotlicious.iot.slackbot.web.dto.DeviceVideoRequestSchema;
import org.springframework.stereotype.Component;

@Component
public class SendLatestDeviceFileCommandMapper {

  /**
   * Constructs a command object from the REST request schema.
   *
   * @param request The REST request payload.
   * @return The command object payload.
   */
  public final SendLatestDeviceFileCommand mapToSendLatestDeviceFileCommand(
      final DeviceVideoRequestSchema request) {
    return ofNullable(request)
        .map(req -> new SendLatestDeviceFileCommand.Builder()
            .title(req.getMessagePlain())
            .deviceId(req.getDeviceId())
            .build())
        .orElse(null);
  }

}
