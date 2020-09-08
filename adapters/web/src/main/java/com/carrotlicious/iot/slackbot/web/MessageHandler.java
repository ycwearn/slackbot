package com.carrotlicious.iot.slackbot.web;

import com.carrotlicious.iot.slackbot.application.port.in.SendMessageUseCase;
import com.carrotlicious.iot.slackbot.web.dto.ErrorResponseSchema;
import com.carrotlicious.iot.slackbot.web.dto.MessageRequestSchema;
import com.carrotlicious.iot.slackbot.web.mapper.SendMessageCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MessageHandler {

  private SendMessageUseCase sendMessageUseCase;
  private SendMessageCommandMapper mapper;

  /**
   * Constructs the REST endpoint handler for /api/message.
   *
   * @param sendMessageUseCase The service that send a message to user.
   * @param mapper The mapper to convert a rest dto schema into command object.
   */
  @Autowired
  public MessageHandler(final SendMessageUseCase sendMessageUseCase,
      final SendMessageCommandMapper mapper) {
    this.sendMessageUseCase = sendMessageUseCase;
    this.mapper = mapper;
  }

  /**
   * Handles the REST request to send message to a user channel.
   *
   * @param request The REST message request payload.
   * @return The REST response payload.
   */
  public Mono<ServerResponse> sendMessage(final ServerRequest request) {
    return request.bodyToMono(MessageRequestSchema.class)
        .map(mapper::mapToSendMessageCommand)
        .flatMap(sendMessageUseCase::sendMessage)
        .flatMap(resp -> ServerResponse.ok().body(BodyInserters.fromObject(resp)))
        .onErrorResume(RuntimeException.class, e ->
            ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BodyInserters.fromObject(this.mapToErrorResponseSchema(e)))
        );
  }

  private ErrorResponseSchema mapToErrorResponseSchema(final RuntimeException e) {
    return new ErrorResponseSchema.Builder().error(e.getMessage()).build();
  }


}
