package com.carrotlicious.iot.slackbot.slack.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class TestSlackFailureException {

  private static final String ERROR_MESSAGE = "ERROR_MESSAGE_1";
  private static final String[] ERROR_DETAILS = new String[] {"ERROR LINE 1", "ERROR LINE 2"};
  private static final String ERROR_ORIGIN = "SLACK_GATEWAY";

  @Test
  public void testExceptionCreation() {
    final SlackFailureException exception =
        new SlackFailureException(ERROR_MESSAGE, Arrays.asList(ERROR_DETAILS));

    assertThat(exception.getMessage()).as("Verify error message.").matches(ERROR_MESSAGE);
    assertThat(exception.getOrigin()).as("Verify error origin.").matches(ERROR_ORIGIN);
    assertThat(exception.getErrorDetails())
        .as("Verify error details.")
        .containsSequence(ERROR_DETAILS);
  }
}
