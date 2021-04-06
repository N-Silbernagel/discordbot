package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class MinTest {
  @ParameterizedTest
  @MethodSource("dataProvider")
  void it_validates_if_an_input_is_bigger_than_a_threshold(String paramValue, int minValue, boolean expected) {
    CommandParam commandParam = new CommandParam(paramValue);

    Min min = new Min(minValue);

    assertEquals(expected, min.validate(commandParam));
  }

  public static Object[][] dataProvider() {
    return new Object[][] {
        {null, -1, true},
        {null, 4, true},
        {"9", 10, false},
        {"11", 10, true},
        {"abc", 2, true},
        {"test", 5, false}
    };
  }
}