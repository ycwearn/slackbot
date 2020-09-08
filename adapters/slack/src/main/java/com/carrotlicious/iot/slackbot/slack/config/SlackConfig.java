package com.carrotlicious.iot.slackbot.slack.config;

import java.net.InetSocketAddress;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

@Configuration
@ConfigurationProperties(prefix = "slack")
public class SlackConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(SlackConfig.class);

  private String baseUrl;
  private String token;
  private String channel;
  private String proxyHost;
  private int proxyPort = 0;

  /**
   * Constructs a slack web client to communicate with slack rest API.
   * @return The slack web client.
   */
  @Bean
  public WebClient getWebClient() {
    return WebClient.builder()
        .clientConnector(this.getClientConnector())
        .baseUrl(this.baseUrl)
        .defaultHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", this.token))
        .build();
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(final String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getToken() {
    return token;
  }

  public void setToken(final String token) {
    this.token = token;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(final String channel) {
    this.channel = channel;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public void setProxyHost(final String proxyHost) {
    this.proxyHost = proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(final int proxyPort) {
    this.proxyPort = proxyPort;
  }

  private ClientHttpConnector getClientConnector() {
    final HttpClient httpClient = Optional.ofNullable(this.proxyHost)
        .filter(StringUtils::hasText)
        .map(proxyHost -> {
          LOGGER.debug("Setup webclient with proxy => {}:{}", this.proxyHost, this.proxyPort);
          return HttpClient.create().tcpConfiguration(tcpClient -> tcpClient.proxy(
              proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                  .address(new InetSocketAddress(proxyHost, this.proxyPort))));
        }).orElse(HttpClient.create());

    return new ReactorClientHttpConnector(httpClient);
  }
}