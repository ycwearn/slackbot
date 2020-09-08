package com.carrotlicious.iot.slackbot.filewatcher.conf;

import static org.assertj.core.api.Assertions.assertThat;

import com.carrotlicious.iot.slackbot.filewatcher.config.FileWatcherConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@EnableAutoConfiguration
@SpringBootTest(classes = {FileWatcherConfig.class})
public class TestFileWatcherConfig {

  @Autowired
  FileWatcherConfig fileWatcherConfig;

  @Test
  public void testFileWatcherConfigurationLoad() {
    assertThat(fileWatcherConfig.getCacheSize()).isEqualTo(2);

    assertThat(fileWatcherConfig.getDeviceDirectories())
        .hasSize(2)
        .containsEntry("DUMMY1", "/mnt/DUMMY1")
        .containsEntry("DUMMY2", "/mnt/DUMMY2");
  }

}
