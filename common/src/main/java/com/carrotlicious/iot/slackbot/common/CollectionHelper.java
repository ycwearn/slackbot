package com.carrotlicious.iot.slackbot.common;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CollectionHelper {

  public static <T> List<T> nullSafeList(List<T> inputCollection) {
    return Optional.ofNullable(inputCollection).orElse(Collections.emptyList());
  }

  public static <T> Set<T> nullSafeCollection(Set<T> inputCollection) {
    return Optional.ofNullable(inputCollection).orElse(Collections.emptySet());
  }

}
