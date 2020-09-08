package com.carrotlicious.iot.slackbot.common.exception;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

public class TestBaseException {

  private static final String TEST_EXCEPTION_MESSAGE = "MOCK_ERROR_MESSAGE";
  private static final String TEST_EXCEPTION_ORIGIN = "MOCK_ERROR_ORIGIN";
  private static final String TEST_EXCEPTION_DETAIL1 = "MOCK_ERROR_DETAIL_1";

  @Test
  public void constructor_givenErrorMessageOriginAndDetail_shouldMappedToExceptionFields() {
    final BaseException exception = new TestException();

    assertThat(exception)
        .as("Verify error message and origin")
        .hasMessage(TEST_EXCEPTION_MESSAGE)
        .hasFieldOrPropertyWithValue("origin", TEST_EXCEPTION_ORIGIN);

    assertThat(exception.getErrorDetails())
        .as("Verify error details")
        .containsExactly(TEST_EXCEPTION_DETAIL1);
  }

  @Test
  public void constructor_givenErrorMessageCauseOriginAndDetail_shouldMappedToExceptionFields() {
    final BaseException exception = new TestException(new IOException());

    assertThat(exception)
        .as("Verify error message, cause and origin")
        .hasMessage(TEST_EXCEPTION_MESSAGE)
        .hasCauseInstanceOf(IOException.class)
        .hasFieldOrPropertyWithValue("origin", TEST_EXCEPTION_ORIGIN);

    assertThat(exception.getErrorDetails())
        .as("Verify error details")
        .containsExactly(TEST_EXCEPTION_DETAIL1);
  }

  static class TestException extends BaseException {
    public TestException() {
      super(TEST_EXCEPTION_MESSAGE, TEST_EXCEPTION_ORIGIN, singletonList(TEST_EXCEPTION_DETAIL1));
    }

    public TestException(final Throwable t) {
      super(
          TEST_EXCEPTION_MESSAGE, t, TEST_EXCEPTION_ORIGIN, singletonList(TEST_EXCEPTION_DETAIL1));
    }
  }
}
