package com.carrotlicious.iot.slackbot.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestJsonHelper {

  private static final String TEST_NAME_VALUE = "test json";

  @Mock private ObjectWriter writer;

  @Test
  public void safeWriteValueAsString_givenObjectWithDate_shouldReturnJsonWithFormattedDate() {
    final LocalDateTime curDatetime = LocalDateTime.now();
    final Map<String, Object> testMap = new HashMap<>();
    testMap.put("name", TEST_NAME_VALUE);
    testMap.put("date", Date.from(curDatetime.atZone(ZoneId.systemDefault()).toInstant()));

    final String result = JsonHelper.safeWriteValueAsString(testMap, "");

    assertThat(result).as("Verify json string is not empty").isNotBlank();

    final DocumentContext ctx = JsonPath.parse(result);
    assertThat(ctx.read("$.name", String.class))
        .as("Verify json string field")
        .isEqualTo(TEST_NAME_VALUE);
    assertThat(ctx.read("$.date", String.class))
        .as("Verify json date time field")
        .isEqualTo(curDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
  }

  @Test
  public void safeWriteValueAsString_givenNull_shouldReturnDefaultValue() {
    final String result = JsonHelper.safeWriteValueAsString(null, "");
    assertThat(result).as("Verify json string is empty").isBlank();
  }

  @Test
  public void safeWriteValueAsString_givenMapperError_shouldReturnDefaultValue()
      throws JsonProcessingException {
    Mockito.doThrow(JsonProcessingException.class).when(writer).writeValueAsString(Mockito.any());

    final String result =
        JsonHelper.safeWriteValueAsString(Maps.newHashMap("name", TEST_NAME_VALUE), "", writer);
    assertThat(result).as("Verify json string is empty when exception").isBlank();
  }
}
