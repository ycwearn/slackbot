package com.carrotlicious.iot.slackbot.application.port.out;

import com.carrotlicious.iot.slackbot.domain.DeviceFile;
import reactor.core.publisher.Mono;

public interface FetchLatestDeviceFilePort {

  Mono<DeviceFile> fetchLatest(final String deviceId);
}
