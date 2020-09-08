package com.carrotlicious.iot.slackbot.common.exception;

import java.util.List;

public abstract class BaseException extends RuntimeException {

  private static final long serialVersionUID = 8465241846938603416L;

  private String origin;
  private List<String> errorDetails;

  /**
   * Constructs a new BaseException with error message, exception, the error origin and a list of
   * error details.
   *
   * @param message The error message.
   * @param origin The error originated component.
   * @param errorDetails The list of error details.
   */
  public BaseException(final String message, final String origin, final List<String> errorDetails) {
    super(message);
    this.origin = origin;
    this.errorDetails = errorDetails;
  }

  /**
   * Constructs a new BaseException with error message, exception, the error origin and a list of
   * error details.
   *
   * @param message The error message.
   * @param cause The error exception stacktrace.
   * @param origin The error originated component.
   * @param errorDetails The list of error details.
   */
  public BaseException(
      final String message,
      final Throwable cause,
      final String origin,
      final List<String> errorDetails) {
    super(message, cause);
    this.origin = origin;
    this.errorDetails = errorDetails;
  }

  public String getOrigin() {
    return origin;
  }

  public List<String> getErrorDetails() {
    return errorDetails;
  }
}
