package com.carrotlicious.iot.slackbot;

import com.carrotlicious.iot.slackbot.filewatcher.DeviceFileRepository;
import java.io.IOException;
import java.nio.file.WatchService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  @Autowired public DeviceFileRepository deviceFileRepository;

  public WatchService watchService;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /**
   * Start up the file repository watchers.
   */
  @PostConstruct
  public void init() {
    this.deviceFileRepository.init();
  }

  /**
   * Clean up background resources used by the application such as the file repository watcher and
   * OS watch services.
   */
  @PreDestroy
  public void cleanup() {
    this.deviceFileRepository.cleanup();
    try {
      this.watchService.close();
    } catch (final IOException e) {
      LOGGER.error("Fail to close watch service. | errorMessage: {}", e.getMessage(), e);
    }
  }
}
