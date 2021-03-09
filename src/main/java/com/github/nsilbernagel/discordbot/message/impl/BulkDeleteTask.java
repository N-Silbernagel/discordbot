package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class BulkDeleteTask extends MessageTask {

  public final static String KEYWORD = "delete";

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  @CommandParam(pos = 0)
  @Required("Bitte gib eine Zahl an.")
  @Numeric(value = "Bitte gib eine Zahl zwischen 1 und 10 an.", min = 1, max = 10)
  private long numberOfMessagesToDelete;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    Mono<Void> bulkDeleteMono = this.messageCreateEventListener.getMessageChannel()
        .getMessagesBefore(this.getMessage().getId())
        .take(numberOfMessagesToDelete)
        .flatMap(Message::delete)
        .then();

    Mono<Void> deleteThisMessageMono = this.getMessage().delete();

    bulkDeleteMono.and(deleteThisMessageMono).block();
  }
}
