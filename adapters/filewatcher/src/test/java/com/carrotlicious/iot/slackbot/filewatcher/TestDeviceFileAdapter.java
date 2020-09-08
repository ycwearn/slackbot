package com.carrotlicious.iot.slackbot.filewatcher;

import static org.mockito.Mockito.doReturn;

import com.carrotlicious.iot.slackbot.application.port.out.FetchLatestDeviceFilePort;
import com.carrotlicious.iot.slackbot.filewatcher.dto.FileMetadata;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = {DeviceFileAdapter.class})
public class TestDeviceFileAdapter {

  private static final Path DEVICE_FILE_PATH = Paths.get("/DUMMY1/20190819/01/03.mp4");

  @MockBean
  private DeviceFileRepository deviceFileRepository;

  @Autowired
  private FetchLatestDeviceFilePort fetchLatestDeviceFilePort;

  @BeforeEach
  public void init() {
    doReturn(this.mockFileMetadata()).when(deviceFileRepository)
        .getLatestByDeviceId(Mockito.eq("DUMMY1"));
  }

  @Test
  public void testFetchLatestDeviceFile() {
    StepVerifier.create(fetchLatestDeviceFilePort.fetchLatest("DUMMY1"))
        .expectNextMatches(actual ->
            actual.getDeviceId().equals("DUMMY1")
                && this.safeIsSameFile(DEVICE_FILE_PATH, actual.getFile()))
        .expectComplete()
        .verify();
  }

  @Test
  public void testFetchLatestDeviceFile_Empty() {
    StepVerifier.create(fetchLatestDeviceFilePort.fetchLatest("DUMMY2"))
        .verifyComplete();
  }

  private boolean safeIsSameFile(final Path expected, final Path actual) {
    try {
      return Files.isSameFile(expected, actual);
    } catch (IOException e) {
      return false;
    }
  }

  private FileMetadata mockFileMetadata() {
    return new FileMetadata.Builder()
        .deviceFile(DEVICE_FILE_PATH)
        .timestamp(LocalDateTime.now())
        .build();
  }

}
