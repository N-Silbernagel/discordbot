package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.message.validation.rules.Numeric;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumericTest {

  @Test
  void it_returns_true_for_numeric_command_param() {
    CommandParam numericCommandParam = new CommandParam("123");

    Numeric numeric = new Numeric();

    assertTrue(numeric.validate(numericCommandParam));
  }

  @Test
  public void it_returns_false_for_non_numeric_command_param() {
    CommandParam numericCommandParam = new CommandParam("abc");

    Numeric numeric = new Numeric();

    assertFalse(numeric.validate(numericCommandParam));
  }

  // as there is the required rule for mandatory command params, it is not the numeric rules responsibility to fail on null values
  @Test
  public void it_returns_true_for_null_command_params() {
    CommandParam numericCommandParam = new CommandParam(null);

    Numeric numeric = new Numeric();

    assertTrue(numeric.validate(numericCommandParam));
  }
}