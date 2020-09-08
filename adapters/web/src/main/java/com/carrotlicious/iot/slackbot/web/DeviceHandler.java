package com.carrotlicious.iot.slackbot.web;

import com.carrotlicious.iot.slackbot.application.port.in.SendLastestDeviceFileUseCase;
import com.carrotlicious.iot.slackbot.application.port.in.SendLastestDeviceFileUseCase.MissingDeviceIdException;
import com.carrotlicious.iot.slackbot.web.dto.DeviceVideoRequestSchema;
import com.carrotlicious.iot.slackbot.web.dto.ErrorResponseSchema;
import com.carrotlicious.iot.slackbot.web.mapper.SendLatestDeviceFileCommandMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class DeviceHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceHandler.class);

  private SendLastestDeviceFileUseCase sendLastestDeviceFileUseCase;
  private SendLatestDeviceFileCommandMapper mapper;

  /**
   * Constructs the REST endpoint handler for /api/device.
   *
   * @param sendLastestDeviceFileUseCase The service that send latest device attachment.
   * @param mapper The mapper to convert a rest dto schema into command object.
   */
  @Autowired
  public DeviceHandler(
      final SendLastestDeviceFileUseCase sendLastestDeviceFileUseCase,
      final SendLatestDeviceFileCommandMapper mapper) {
    this.sendLastestDeviceFileUseCase = sendLastestDeviceFileUseCase;
    this.mapper = mapper;
  }

  /**
   * Handles the REST request to send device attachment to a user channel.
   *
   * @param request The REST device attachment request payload.
   * @return The REST response payload.
   */
  public Mono<ServerResponse> sendLatestDeviceFile(final ServerRequest request) {
    return request.bodyToMono(DeviceVideoRequestSchema.class)
        .doOnNext(req -> LOGGER.debug("Received deviceVideoRequest : {}", req))
        .map(this.mapper::mapToSendLatestDeviceFileCommand)
        .flatMap(this.sendLastestDeviceFileUseCase::sendLastestDeviceFile)
        .flatMap(resp -> ServerResponse.ok().body(BodyInserters.fromObject(resp)))
        .onErrorResume(MissingDeviceIdException.class, mdiEx ->
            ServerResponse
                .status(HttpStatus.BAD_REQUEST)
                .body(BodyInserters.fromObject(this.mapToErrorResponseSchema(mdiEx))))
        .onErrorResume(RuntimeException.class, e ->
            ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BodyInserters.fromObject(this.mapToErrorResponseSchema(e)))
        );
  }

  private ErrorResponseSchema mapToErrorResponseSchema(final MissingDeviceIdException e) {
    return new ErrorResponseSchema.Builder()
        .error(e.getMessage()).errorDetails(e.getErrorDetails())
        .build();
  }

  private ErrorResponseSchema mapToErrorResponseSchema(final RuntimeException e) {
    return new ErrorResponseSchema.Builder().error(e.getMessage()).build();
  }

}
