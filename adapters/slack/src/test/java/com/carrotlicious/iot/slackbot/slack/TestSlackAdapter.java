package com.carrotlicious.iot.slackbot.slack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;

import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import com.carrotlicious.iot.slackbot.slack.exception.SlackFailureException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.test.StepVerifier;

public class TestSlackAdapter {

  private static final String MOCK_ERROR_MESSAGE = "MOCK_ERROR_MESSAGE";
  private static final String[] ERROR_MESSAGES = new String[]{"ERROR LINE 1", "ERROR LINE 2"};

  @Mock
  SlackAdapter slackAdapter;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
    doCallRealMethod().when(slackAdapter).handleResponse(any(MessageResponse.class));
  }

  @Test
  public void testSuccessHandleResponse() {
    StepVerifier.create(slackAdapter.handleResponse(this.mockSuccessMessageResponse()))
        .verifyComplete();
  }

  @Test
  public void testErrorHandleResponse() {
    StepVerifier.create(slackAdapter.handleResponse(this.mockFailureMessageResponse()))
        .verifyErrorSatisfies(e -> {
          assertThat(e.getClass()).hasSameClassAs(SlackFailureException.class);
          assertThat(e.getMessage()).matches(MOCK_ERROR_MESSAGE);
          assertThat(((SlackFailureException) e).getErrorDetails())
              .containsSequence(ERROR_MESSAGES);

        });
  }

  private MessageResponse mockSuccessMessageResponse() {
    return new MessageResponse.Builder().ok(true).build();
  }

  private MessageResponse mockFailureMessageResponse() {
    final Map<String, Object> responseMetadata = new HashMap<>();
    responseMetadata.put("messages", Arrays.asList(ERROR_MESSAGES));

    return new MessageResponse.Builder()
        .ok(false)
        .error(MOCK_ERROR_MESSAGE)
        .responseMetadata(responseMetadata)
        .build();
  }

}
