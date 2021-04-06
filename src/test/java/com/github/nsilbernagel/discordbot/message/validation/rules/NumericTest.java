package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class NumericTest {
  @ParameterizedTest
  @MethodSource("dataProvider")
  void it_validates_if_an_input_is_numeric(String paramValue, boolean expected) {
    CommandParam commandParam = new CommandParam(paramValue);

    Numeric numeric = new Numeric();

    assertEquals(expected, numeric.validate(commandParam));
  }

  public static Object[][] dataProvider() {
    return new Object[][] {
        {null,  true},
        {Integer.MIN_VALUE + "", true},
        {Integer.MAX_VALUE + "", true},
        {Long.MAX_VALUE + "", true},
        {Float.MIN_VALUE + "", true},
        {Float.MAX_VALUE + "", true},
        {"abc", false},
        {"test", false},
    };
  }
}