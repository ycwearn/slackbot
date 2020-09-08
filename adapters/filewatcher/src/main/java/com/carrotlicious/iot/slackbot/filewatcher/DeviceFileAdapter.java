package com.carrotlicious.iot.slackbot.filewatcher;

import com.carrotlicious.iot.slackbot.application.port.out.FetchLatestDeviceFilePort;
import com.carrotlicious.iot.slackbot.domain.DeviceFile;
import com.carrotlicious.iot.slackbot.filewatcher.dto.FileMetadata;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DeviceFileAdapter implements FetchLatestDeviceFilePort {

  private DeviceFileRepository deviceFileRepository;

  @Autowired
  public DeviceFileAdapter(final DeviceFileRepository deviceFileRepository) {
    this.deviceFileRepository = deviceFileRepository;
  }

  @Override
  public Mono<DeviceFile> fetchLatest(final String deviceId) {
    Optional<DeviceFile> deviceFile = Optional
        .ofNullable(this.deviceFileRepository.getLatestByDeviceId(deviceId))
        .map(fileMeta -> this.mapToDeviceFile(deviceId, fileMeta));

    return Mono.justOrEmpty(deviceFile);
  }

  private DeviceFile mapToDeviceFile(final String deviceId, final FileMetadata fileMetadata) {
    return new DeviceFile.Builder()
        .deviceId(deviceId)
        .timestamp(fileMetadata.getTimestamp())
        .file(fileMetadata.getDeviceFile())
        .build();
  }
}
