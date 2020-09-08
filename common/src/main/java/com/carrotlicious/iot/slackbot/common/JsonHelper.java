package com.carrotlicious.iot.slackbot.common;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonHelper.class);

  private static final ObjectMapper mapper =
      new ObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

  /**
   * Writes a JSON text for a given object.
   *
   * @param value The object to convert into JSON text.
   * @param defaultValue The default value to use if there is any error in the JSON conversion.
   * @return The JSON text for given object.
   */
  public static String safeWriteValueAsString(final Object value, final String defaultValue) {
    return JsonHelper.safeWriteValueAsString(
        value, defaultValue, JsonHelper.mapper.writerWithDefaultPrettyPrinter());
  }

  /**
   * Writes a JSON text for a given object with provided writer.
   * @param value The object to convert into JSON text.
   * @param defaultValue The default value to use if there is any error in the JSON conversion.
   * @param writer The writer implementation.
   * @return The JSON text for given object.
   */
  public static String safeWriteValueAsString(
      final Object value, final String defaultValue, final ObjectWriter writer) {
    return ofNullable(value)
        .map(
            input -> {
              try {
                return writer.writeValueAsString(input);
              } catch (final JsonProcessingException e) {
                LOGGER.error(
                    "Fail to convert object to json. | ErrorMessage: {}", e.getMessage(), e);
              }
              return defaultValue;
            })
        .orElse(defaultValue);
  }
}
