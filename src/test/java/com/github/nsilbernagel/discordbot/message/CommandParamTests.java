package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.message.validation.MessageValidationException;
import com.github.nsilbernagel.discordbot.message.validation.rules.Numeric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommandParamTests {
  @Test
  public void it_can_be_validated_against_a_validation_rule(){
    CommandParam commandParam = new CommandParam("test");

    assertThrows(MessageValidationException.class, () -> commandParam.is(new Numeric(), "Fail"));
  }

  @Test
  public void the_validation_error_message_can_be_specified(){
    CommandParam commandParam = new CommandParam("test");

    String expectedErrorMessage = "Fail";

    assertThrows(MessageValidationException.class, () -> commandParam.is(new Numeric(), expectedErrorMessage), expectedErrorMessage);
  }

  @Test
  public void its_raw_value_can_be_cast_to_another_class() {
    CommandParam commandParam = new CommandParam("123");

    assertDoesNotThrow(() -> {
      commandParam.as(Integer.class);
    });
  }
}
