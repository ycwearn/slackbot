package com.carrotlicious.iot.slackbot.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class TestFilesHelper {

  @Test
  public void safeIsSameFile_givenSameFilePath_shouldReturnTrue() {
    final Path dummyPath = Paths.get("tmp", "dummy.txt");
    boolean result = FilesHelper.safeIsSameFile(dummyPath, dummyPath);

    assertThat(result).as("Same path").isTrue();
  }

  @Test
  public void safeIsSameFile_givenDifferentFilePath_shouldReturnFalse() {
    final Path dummyPath = Paths.get("tmp", "dummy.txt");
    final Path nonDummyPath = Paths.get("tmp", "non_dummy.txt");
    boolean result = FilesHelper.safeIsSameFile(dummyPath, nonDummyPath);

    assertThat(result).as("Different path").isFalse();
  }

  @Test
  public void safeIsSameFile_givenNullPath_shouldReturnFalse() {
    final Path dummyPath = Paths.get("tmp", "dummy.txt");
    boolean result = FilesHelper.safeIsSameFile(dummyPath, null);

    assertThat(result).as("One of the path is null").isFalse();
  }
}
