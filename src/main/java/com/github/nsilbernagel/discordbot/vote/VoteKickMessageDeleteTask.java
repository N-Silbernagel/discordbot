package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.message.MessageDeleteTask;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import org.springframework.stereotype.Component;

@Component
public class VoteKickMessageDeleteTask extends MessageDeleteTask {
  @Override
  public void execute(MessageDeleteEvent messageDeleteEvent) {
    System.out.println("deleted");
  }
}
