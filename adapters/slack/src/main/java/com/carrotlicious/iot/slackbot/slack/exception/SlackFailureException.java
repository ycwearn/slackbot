package com.carrotlicious.iot.slackbot.slack.exception;

import com.carrotlicious.iot.slackbot.common.exception.BaseException;
import java.util.List;

public class SlackFailureException extends BaseException {

  private static final long serialVersionUID = -8231713031205162475L;

  private static final String ORIGIN = "SLACK_GATEWAY";

  public SlackFailureException(String error, List<String> errorDetails) {
    super(error, ORIGIN, errorDetails);
  }

}
