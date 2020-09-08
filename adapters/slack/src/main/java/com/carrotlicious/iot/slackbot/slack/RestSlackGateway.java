package com.carrotlicious.iot.slackbot.slack;

import com.carrotlicious.iot.slackbot.slack.dto.FileUploadRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageRequest;
import com.carrotlicious.iot.slackbot.slack.dto.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class RestSlackGateway implements SlackGateway {

  private static final Logger LOGGER = LoggerFactory.getLogger(RestSlackGateway.class);

  private static final String API_CHAT_POSTMESSAGE = "chat.postMessage";
  private static final String API_FILES_UPLOAD = "files.upload";

  private WebClient slackClient;

  @Autowired
  public RestSlackGateway(final WebClient slackClient) {
    this.slackClient = slackClient;
  }

  @Override
  public Mono<MessageResponse> sendMessage(final MessageRequest messageRequest) {
    return this.slackClient.post().uri(API_CHAT_POSTMESSAGE)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromObject(messageRequest))
        .retrieve()
        .bodyToMono(MessageResponse.class);
  }

  @Override
  public Mono<MessageResponse> sendAttachment(final FileUploadRequest fileUploadRequest) {
    return this.slackClient.post().uri(API_FILES_UPLOAD)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(mapToMultipart(fileUploadRequest)))
        .retrieve()
        .bodyToMono(MessageResponse.class);
  }

  protected MultiValueMap<String, HttpEntity<?>> mapToMultipart(
      final FileUploadRequest fileUploadRequest) {
    final MultipartBodyBuilder builder = new MultipartBodyBuilder();

    final String fileContentDisposition =
        String.format("form-data; name=file; filename=%s", fileUploadRequest.getFilename());

    builder.part("file", Base64Utils.decodeFromString(fileUploadRequest.getBase64()))
        .header(HttpHeaders.CONTENT_DISPOSITION, fileContentDisposition);

    builder.part("channels", fileUploadRequest.getChannel(), MediaType.TEXT_PLAIN);
    builder.part("title", fileUploadRequest.getTitle(), MediaType.TEXT_PLAIN);

    return builder.build();
  }
}
