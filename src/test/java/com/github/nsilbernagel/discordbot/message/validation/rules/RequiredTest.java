package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RequiredTest {
  @ParameterizedTest
  @MethodSource("dataProvider")
  void it_validates_if_an_input_is_present(String paramValue, boolean expected) {
    CommandParam commandParam = new CommandParam(paramValue);

    Required required = new Required();

    assertEquals(expected, required.validate(commandParam));
  }

  public static Object[][] dataProvider() {
    return new Object[][] {
        {null, false},
        {"9", true},
        {"-11", true},
        {"abc", true},
        {"test", true}
    };
  }
}