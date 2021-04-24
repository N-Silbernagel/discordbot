package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MaxTest {
  @ParameterizedTest
  @MethodSource("dataProvider")
  public void it_validates_if_an_input_is_smaller_than_a_threshold(String paramValue, int maxValue, boolean expected) {
    CommandParam commandParam = new CommandParam(paramValue);

    Max max = new Max(maxValue);

    assertEquals(expected, max.validate(commandParam));
  }

  public static Object[][] dataProvider() {
    return new Object[][] {
        {null, -1, true},
        {null, 4, true},
        {"9", 10, true},
        {"11", 10, false},
        {"abc", 2, false},
        {"test", 5, true}
    };
  }
}