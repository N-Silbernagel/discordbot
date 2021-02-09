package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.FalseInputException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class BulkDeleteTask extends AbstractMessageTask {

  public final static String KEYWORD = "delete";

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    if (this.messageToTaskHandler.getCommandParameters().size() == 0) {
      throw new FalseInputException("Bitte gib eine Zahl an.");
    }

    String messagesCountString = this.messageToTaskHandler.getCommandParameters().get(0);

    Integer messagesCount;

    try {
      messagesCount = Integer.parseInt(messagesCountString);
    } catch (NumberFormatException e) {
      throw new FalseInputException("Bitte gib eine Zahl an.");
    }

    if (messagesCount < 1 || messagesCount > 10) {
      throw new FalseInputException("Bitte gib eine Zahl zwischen 1 und 10 an.");
    }

    Mono<Void> bulkDeleteMono = this.messageCreateEventListener.getMessageChannel()
        .getMessagesBefore(this.getMessage().getId())
        .take(messagesCount)
        .flatMap(foundMessage -> foundMessage.delete())
        .then();

    Mono<Void> deleteThisMessageMono = this.getMessage().delete();

    bulkDeleteMono.and(deleteThisMessageMono).block();
  }
}
