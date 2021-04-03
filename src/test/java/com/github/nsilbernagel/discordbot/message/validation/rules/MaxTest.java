package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxTest {
  @Test
  void it_returns_true_for_numerics_smaller_than_threshold() {
    CommandParam smallerCommandParam = new CommandParam("9");

    Max max = new Max(10);

    assertTrue(max.validate(smallerCommandParam));
  }

  @Test
  public void it_returns_false_for_numerics_bigger_than_threshold() {
    CommandParam biggerCommandParam = new CommandParam("11");

    Max max = new Max(10);

    assertFalse(max.validate(biggerCommandParam));
  }

  // as there is the required rule for mandatory command params, it is not the max rule's responsibility to fail on null values
  @Test
  public void it_returns_true_for_null_command_params() {
    CommandParam nullCommandParam = new CommandParam(null);

    Max max = new Max(1);

    assertTrue(max.validate(nullCommandParam));
  }

  @Test
  public void it_validates_a_strings_length() {
    String testString = "test";
    CommandParam commandParam = new CommandParam(testString);

    Max fails = new Max(testString.length() - 1);
    Max passes = new Max(testString.length());

    assertTrue(passes.validate(commandParam));
    assertFalse(fails.validate(commandParam));
  }
}