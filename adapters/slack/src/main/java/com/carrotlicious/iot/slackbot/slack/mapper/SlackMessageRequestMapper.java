package com.carrotlicious.iot.slackbot.slack.mapper;

import com.carrotlicious.iot.slackbot.domain.Message;
import com.carrotlicious.iot.slackbot.domain.MessageAction;
import com.carrotlicious.iot.slackbot.slack.dto.MessageRequest;
import com.carrotlicious.iot.slackbot.slack.dto.block.Actions;
import com.carrotlicious.iot.slackbot.slack.dto.block.BaseBlock;
import com.carrotlicious.iot.slackbot.slack.dto.block.Context;
import com.carrotlicious.iot.slackbot.slack.dto.block.Divider;
import com.carrotlicious.iot.slackbot.slack.dto.block.Section;
import com.carrotlicious.iot.slackbot.slack.dto.element.BaseElement;
import com.carrotlicious.iot.slackbot.slack.dto.element.ButtonElement;
import com.carrotlicious.iot.slackbot.slack.dto.element.MarkdownElement;
import com.carrotlicious.iot.slackbot.slack.dto.element.TextElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class SlackMessageRequestMapper {

  /**
   * Constructs a dto from domain object.
   *
   * @param message The slack message payload.
   * @param channel The slack channel.
   * @return The slack message dto to send a slack message.
   */
  public MessageRequest mapToMessageRequest(final Message message, final String channel) {
    return Optional.ofNullable(message)
        .map(req ->
            new MessageRequest.Builder().channel(channel)
                .text(message.getMessagePlain())
                .blocks(mapMessageBlocks(message.getMessageMarkdown(), message.getActions()))
                .build()
        )
        .orElse(null);
  }

  private List<BaseBlock> mapMessageBlocks(final String markdown,
      final List<MessageAction> actions) {
    final List<BaseBlock> result = new ArrayList<>();

    result.add(this.createMarkdownSection(markdown));

    if (!CollectionUtils.isEmpty(actions)) {
      result.add(new Divider());

      final List<BaseElement> actionList = actions.stream()
          .map(action -> new ButtonElement.Builder()
              .actionId(action.getId())
              .value(action.getValue())
              .text(new TextElement.Builder().text(action.getDisplay()).build())
              .build())
          .collect(Collectors.toList());
      result.add(new Actions.Builder().elements(actionList).build());
    }
    result.add(this.createLastUpdatedContext());

    return result;
  }

  private Context createLastUpdatedContext() {
    final String lastUpdatedText = String.format("*Last Updated:* %s",
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")));

    final MarkdownElement mdElement = new MarkdownElement.Builder().text(lastUpdatedText).build();

    return new Context.Builder().elements(Collections.singletonList(mdElement)).build();
  }

  private Section createMarkdownSection(final String markdown) {
    return new Section.Builder()
        .text(new MarkdownElement.Builder().text(markdown).build())
        .build();
  }

}
