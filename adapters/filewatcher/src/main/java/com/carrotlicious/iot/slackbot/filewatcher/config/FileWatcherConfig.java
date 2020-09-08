package com.carrotlicious.iot.slackbot.filewatcher.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "filewatcher")
public class FileWatcherConfig {

  private int cacheSize;
  private Map<String, String> deviceDirectories;

  public int getCacheSize() {
    return cacheSize;
  }

  public void setCacheSize(final int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public Map<String, String> getDeviceDirectories() {
    return deviceDirectories;
  }

  public void setDeviceDirectories(final Map<String, String> deviceDirectories) {
    this.deviceDirectories = deviceDirectories;
  }
}
