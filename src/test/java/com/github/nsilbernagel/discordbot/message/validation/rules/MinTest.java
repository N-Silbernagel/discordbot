package com.github.nsilbernagel.discordbot.message.validation.rules;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinTest {
  @Test
  void it_returns_true_for_numerics_bigger_than_threshold() {
    CommandParam biggerCommandParam = new CommandParam("9");

    Min min = new Min(8);

    assertTrue(min.validate(biggerCommandParam));
  }

  @Test
  public void it_returns_false_for_numerics_small_than_threshold() {
    CommandParam smallerCommandParam = new CommandParam("9");

    Min min = new Min(10);

    assertFalse(min.validate(smallerCommandParam));
  }

  // as there is the required rule for mandatory command params, it is not the min rule's responsibility to fail on null values
  @Test
  public void it_returns_true_for_null_command_params() {
    CommandParam nullCommandParam = new CommandParam(null);

    Min min = new Min(1);

    assertTrue(min.validate(nullCommandParam));
  }

  @Test
  public void it_validates_a_strings_length() {
    String testString = "test";
    CommandParam commandParam = new CommandParam(testString);

    Min fails = new Min(testString.length() + 1);
    Min passes = new Min(testString.length());

    assertTrue(passes.validate(commandParam));
    assertFalse(fails.validate(commandParam));
  }
}