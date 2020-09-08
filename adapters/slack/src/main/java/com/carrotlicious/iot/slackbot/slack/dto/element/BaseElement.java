package com.carrotlicious.iot.slackbot.slack.dto.element;

import com.carrotlicious.iot.slackbot.slack.dto.block.Section;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @Type(value = Section.class, name = "section"),
    @Type(value = ButtonElement.class, name = "button"),
    @Type(value = TextElement.class, name = "plain_text"),
    @Type(value = MarkdownElement.class, name = "mrkdwn")
})
public class BaseElement {

}
