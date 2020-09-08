package com.carrotlicious.iot.slackbot.slack;

import com.carrotlicious.iot.slackbot.slack.dto.FileUploadRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import reactor.core.publisher.Mono;

public interface SlackGateway {

  Mono<MessageResponse> sendMessage(MessageRequest messageRequest);

  Mono<MessageResponse> sendAttachment(FileUploadRequest fileUploadRequest);

}
