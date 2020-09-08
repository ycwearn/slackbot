package com.carrotlicious.iot.slackbot.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TestCollectionHelper {

  @Test
  public void nullSafeList_givenListWithElements_shouldReturnAllElements() {
    final List<String> result = CollectionHelper.nullSafeList(Arrays.asList("one", "two", "three"));
    assertThat(result)
        .as("Verify non empty list")
        .hasSize(3)
        .containsExactly("one", "two", "three");
  }

  @Test
  public void nullSafeList_givenNull_shouldReturnEmptyList() {
    final List<String> result = CollectionHelper.nullSafeList(null);
    assertThat(result).as("Verify empty list").hasSize(0);
  }

  @Test
  public void nullSafeCollection_givenSetWithElements_shouldReturnAllElements() {
    final Set<String> result = CollectionHelper.nullSafeCollection(Set.of("one", "two", "three"));
    assertThat(result).as("Verify non empty set").hasSize(3).containsOnly("one", "two", "three");
  }

  @Test
  public void nullSafeCollection_givenNull_shouldReturnEmptySet() {
    final Set<String> result = CollectionHelper.nullSafeCollection(null);
    assertThat(result).as("Verify empty set").hasSize(0);
  }
}
