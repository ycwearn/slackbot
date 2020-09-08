package com.carrotlicious.iot.slackbot.web.mapper;

import static com.carrotlicious.iot.slackbot.common.CollectionHelper.nullSafeList;

import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageAction;
import com.carrotlicious.iot.slackbot.application.port.in.command.SendMessageCommand;
import com.carrotlicious.iot.slackbot.web.dto.ActionSchema;
import com.carrotlicious.iot.slackbot.web.dto.MessageRequestSchema;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SendMessageCommandMapper {

  /**
   * Constructs a command object from the REST request schema.
   *
   * @param request The REST request payload.
   * @return The command object payload.
   */
  public final SendMessageCommand mapToSendMessageCommand(final MessageRequestSchema request) {
    return Optional.ofNullable(request)
        .map(req ->
            new SendMessageCommand.Builder()
                .messageMarkdown(req.getMessageMarkdown())
                .messagePlain(req.getMessagePlain())
                .actions(this.mapToSendMessageAction(req.getActions()))
                .build()
        )
        .orElse(null);
  }

  private final List<SendMessageAction> mapToSendMessageAction(final List<ActionSchema> actions) {
    return nullSafeList(actions)
        .stream()
        .map(action ->
            new SendMessageAction.Builder()
                .display(action.getDisplay())
                .value(action.getValue())
                .metadatas(action.getMetadatas())
                .build()
        )
        .collect(Collectors.toList());
  }

}
