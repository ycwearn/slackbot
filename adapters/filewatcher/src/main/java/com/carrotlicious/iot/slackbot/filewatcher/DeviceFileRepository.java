package com.carrotlicious.iot.slackbot.filewatcher;

import com.carrotlicious.iot.slackbot.filewatcher.dto.FileMetadata;

public interface DeviceFileRepository {

  void init();

  void cleanup();

  FileMetadata getLatestByDeviceId(String deviceId);

}
