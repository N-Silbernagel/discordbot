package com.github.nsilbernagel.discordbot.guard;

import discord4j.core.object.entity.Message;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class MessageFilter {
  @Getter
  private final List<String> regexFilters = List.of(".+restposten.de.+", "");

  public void execute(Message message) {
    this.getRegexFilters().forEach(regex -> this.deleteMessageMatchingRegex(message, regex));
  }

  private void deleteMessageMatchingRegex(Message message, String regex) {
    if(message.getContent().matches(regex)){
      message.delete().block();
    }
  }
}
