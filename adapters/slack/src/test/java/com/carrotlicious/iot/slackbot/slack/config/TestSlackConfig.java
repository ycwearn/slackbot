package com.carrotlicious.iot.slackbot.slack.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@EnableAutoConfiguration
@SpringBootTest(classes = SlackConfig.class)
public class TestSlackConfig {

  private static final String EXPECTED_BASE_URL = "https://slack.com/api/";
  private static final String EXPECTED_TOKEN = "dummy_authentication_token";
  private static final String EXPECTED_CHANNEL = "iot_home";
  private static final String EXPECTED_PROXY_HOST = "127.0.0.1";
  private static final int EXPECTED_PROXY_PORT = 8080;

  @Autowired
  SlackConfig slackConfig;

  @Test
  public void testSlackConfigurationLoad() {
    assertThat(slackConfig.getBaseUrl()).matches(EXPECTED_BASE_URL);
    assertThat(slackConfig.getToken()).matches(EXPECTED_TOKEN);
    assertThat(slackConfig.getChannel()).matches(EXPECTED_CHANNEL);
    assertThat(slackConfig.getProxyHost()).matches(EXPECTED_PROXY_HOST);
    assertThat(slackConfig.getProxyPort()).isEqualTo(EXPECTED_PROXY_PORT);
    assertThat(slackConfig.getWebClient()).isNotNull();
  }

}
