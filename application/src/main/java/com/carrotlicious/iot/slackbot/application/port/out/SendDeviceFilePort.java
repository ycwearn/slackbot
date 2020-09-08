package com.carrotlicious.iot.slackbot.application.port.out;

import com.carrotlicious.iot.slackbot.domain.DeviceFileMessage;
import reactor.core.publisher.Mono;

public interface SendDeviceFilePort {

  Mono<Void> sendMessage(DeviceFileMessage message);

}
