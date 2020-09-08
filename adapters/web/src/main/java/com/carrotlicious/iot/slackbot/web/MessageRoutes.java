package com.carrotlicious.iot.slackbot.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class MessageRoutes {

  /**
   * Constructs the HTTP routing rules for /api/message endpoint.
   *
   * @param messageHandler The handler the serve /api/message endpoint traffic.
   * @return The HTTP routing rules.
   */
  @Bean(name = "messageRouter")
  public RouterFunction<ServerResponse> messageRoutes(final MessageHandler messageHandler) {
    return RouterFunctions
        .route(POST("/api/message")
            .and(accept(MediaType.APPLICATION_JSON)), messageHandler::sendMessage);
  }

}
