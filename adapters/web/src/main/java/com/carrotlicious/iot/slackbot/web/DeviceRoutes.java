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
public class DeviceRoutes {

  /**
   * Constructs the HTTP routing rules for /api/device endpoint.
   *
   * @param deviceHandler The handler the serve /api/device endpoint traffic.
   * @return The HTTP routing rules.
   */
  @Bean(name = "deviceRouter")
  public RouterFunction<ServerResponse> deviceRoutes(final DeviceHandler deviceHandler) {
    return RouterFunctions
        .route(POST("/api/device/attachment")
            .and(accept(MediaType.APPLICATION_JSON)), deviceHandler::sendLatestDeviceFile);
  }

}
