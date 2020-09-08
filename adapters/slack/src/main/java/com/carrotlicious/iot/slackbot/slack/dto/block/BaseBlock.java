package com.carrotlicious.iot.slackbot.slack.dto.block;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes({
    @Type(value = Actions.class, name = "actions"),
    @Type(value = Divider.class, name = "divider"),
    @Type(value = Section.class, name = "section"),
    @Type(value = Context.class, name = "context")
})
public abstract class BaseBlock {

}
