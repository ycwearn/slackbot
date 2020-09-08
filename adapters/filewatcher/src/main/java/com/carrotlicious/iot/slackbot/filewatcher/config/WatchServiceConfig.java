package com.carrotlicious.iot.slackbot.filewatcher.config;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatchServiceConfig {

  @Bean
  public WatchService getWatchService() throws IOException {
    return FileSystems.getDefault().newWatchService();
  }
}
