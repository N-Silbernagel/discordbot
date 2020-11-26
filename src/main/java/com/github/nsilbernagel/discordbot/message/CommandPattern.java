package com.github.nsilbernagel.discordbot.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandPattern {
  private String keyword; // Keyword that maps to a IMessageTask implementation.
  private List<String> subcommands = new ArrayList<>(); // Additional info that can be accessed in the tasks.

  public CommandPattern(String keyword, String additionalInfo) {
    this.keyword = keyword;
    parseAdditionalInfo(additionalInfo);
  }

  /*
   * Parse all words of additionalInfo into the subcommands List. Words are split
   * at whitespaces.
   */
  void parseAdditionalInfo(String additionalInfo) {
    if (!additionalInfo.isEmpty()) {
      subcommands = Arrays.asList(additionalInfo.split(" "));
    }
  }
}
