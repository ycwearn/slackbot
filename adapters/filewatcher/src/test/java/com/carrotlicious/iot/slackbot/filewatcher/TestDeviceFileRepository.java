package com.carrotlicious.iot.slackbot.filewatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;

import com.carrotlicious.iot.slackbot.filewatcher.config.FileWatcherConfig;
import com.carrotlicious.iot.slackbot.filewatcher.config.WatchServiceConfig;
import com.carrotlicious.iot.slackbot.filewatcher.dto.FileMetadata;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, TempDirectory.class})
@SpringBootTest(classes = {FileSystemDeviceFileRepository.class, WatchServiceConfig.class,
    DeviceDirectoryWatcherFactory.class})
public class TestDeviceFileRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestDeviceFileRepository.class);
  @MockBean
  private FileWatcherConfig fileWatcherConfig;

  @Autowired
  private DeviceFileRepository deviceFileRepository;

  /**
   * Initialises the temporary test directory  and file watch service for test cases.
   *
   * @param testDir the temporary test directory.
   */
  @BeforeEach
  public void init(@TempDir final Path testDir) {
    doReturn(2).when(fileWatcherConfig).getCacheSize();
    doReturn(this.mockDeviceDirectories(testDir)).when(fileWatcherConfig).getDeviceDirectories();
    this.initiateTestDir();
    this.deviceFileRepository.init();
  }

  /**
   * Cleanup the file watch service for test cases.
   */
  @AfterEach
  public void cleanup() {
    this.deviceFileRepository.cleanup();
  }

  @Test
  public void testGetLatestByDeviceId() {
    final String rootPath = this.fileWatcherConfig.getDeviceDirectories().get("DUMMY1");
    final Path rootDir = Paths.get(rootPath);

    final Path dateDir = rootDir.resolve("20190813");
    this.createDirectoryWithDelay(dateDir);

    final Path hourDir = dateDir.resolve("01");
    this.createDirectoryWithDelay(hourDir);

    final Path minuteFile = hourDir.resolve("15.mp4");
    this.createFileWithDelay(minuteFile);

    final FileMetadata meta = deviceFileRepository.getLatestByDeviceId("DUMMY1");

    Assertions.assertThat(meta)
        .isNotNull()
        .extracting("deviceFile")
        .first()
        .hasToString(minuteFile.toString());
  }

  @Test
  public void testNewHourDirectory() {
    final String rootPath = this.fileWatcherConfig.getDeviceDirectories().get("DUMMY2");
    final Path rootDir = Paths.get(rootPath);

    final Path dateDir = rootDir.resolve("20190813");
    this.createDirectoryWithDelay(dateDir);
    final Path hourDir = dateDir.resolve("01");
    this.createDirectoryWithDelay(hourDir);
    final Path minuteFile = hourDir.resolve("15.mp4");
    this.createFileWithDelay(minuteFile);
    final Path newHourDir = dateDir.resolve("02");
    this.createDirectoryWithDelay(newHourDir);
    final Path newMinuteFile = newHourDir.resolve("10.mp4");
    this.createFileWithDelay(newMinuteFile);
    final Path oldMinuteFile = newHourDir.resolve("09.mp4");
    this.createFileWithDelay(oldMinuteFile);
    // Should not be picked up
    final Path oldHourDirFile = hourDir.resolve("16.mp4");
    this.createFileWithDelay(oldHourDirFile);

    final FileMetadata meta = deviceFileRepository.getLatestByDeviceId("DUMMY2");
    assertNotNull(meta);
    assertNotNull(meta.getDeviceFile());
    assertEquals(newMinuteFile.toString(), meta.getDeviceFile().toString());
  }

  @Test
  public void testInvalidFile() {
    final String rootPath = this.fileWatcherConfig.getDeviceDirectories().get("DUMMY1");
    final Path rootDir = Paths.get(rootPath);

    final Path dateDir = rootDir.resolve("20190813");
    this.createDirectoryWithDelay(dateDir);
    final Path hourDir = dateDir.resolve("01");
    this.createDirectoryWithDelay(hourDir);
    final Path minuteFile = hourDir.resolve("61.mp4");
    this.createFileWithDelay(minuteFile);

    final FileMetadata meta1 = deviceFileRepository.getLatestByDeviceId("DUMMY1");
    assertNull(meta1);

    final Path nonMp4File = hourDir.resolve("59.mp3");
    this.createFileWithDelay(nonMp4File);

    final FileMetadata meta2 = deviceFileRepository.getLatestByDeviceId("DUMMY1");
    assertNull(meta2);
  }

  @Test
  public void testInvalidDevice() {
    final FileMetadata meta1 = deviceFileRepository.getLatestByDeviceId("DUMMY_INVALID");
    assertNull(meta1);
  }

  private Map<String, String> mockDeviceDirectories(final Path rootPath) {
    Map<String, String> deviceDirectories = new HashMap<>();
    deviceDirectories.put("DUMMY1", rootPath.resolve("DUMMY1").toString());
    deviceDirectories.put("DUMMY2", rootPath.resolve("DUMMY2").toString());
    return deviceDirectories;
  }

  private void initiateTestDir() {
    this.fileWatcherConfig.getDeviceDirectories().values().forEach(dir -> {
      final Path dirPath = Paths.get(dir);
      try {
        LOGGER.debug("Creating directory {}.", dirPath);
        Files.createDirectories(dirPath);
      } catch (final IOException e) {
        LOGGER.error("Fail to create device directory: {} | errorMessage: {}", dirPath,
            e.getMessage());
      }
    });
  }

  private void createDirectoryWithDelay(final Path dirPath) {
    try {
      LOGGER.debug("Creating directory {}.", dirPath);
      Files.createDirectories(dirPath);
      Thread.sleep(200L);
    } catch (final InterruptedException | IOException e) {
      e.printStackTrace();
    }
  }

  private void createFileWithDelay(final Path dirPath) {
    try {
      LOGGER.debug("Creating file: {}.", dirPath);
      Files.createFile(dirPath);
      Thread.sleep(200L);
    } catch (final InterruptedException | IOException e) {
      LOGGER.error("Fail to create file: {}.", dirPath, e);
    }
  }

}
