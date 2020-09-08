package com.carrotlicious.iot.slackbot.common;

import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(FilesHelper.class);

  /**
   * Checks if both the given path is pointing to the same file / folder without throwing
   * exceptions.
   *
   * @param firstDir the first path.
   * @param secondPath the second path.
   * @return true if both paths are the same otherwise false.
   */
  public static boolean safeIsSameFile(final Path firstDir, final Path secondPath) {
    try {
      return Files.isSameFile(firstDir, secondPath);
    } catch (final Exception e) {
      LOGGER.error(
          "Fail to compare 2 directories. | {} | {} | ErrorMessage: {}",
          firstDir,
          secondPath,
          e.getMessage());
      return false;
    }
  }
}
