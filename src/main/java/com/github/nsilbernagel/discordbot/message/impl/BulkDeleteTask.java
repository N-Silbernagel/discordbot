package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class BulkDeleteTask extends MessageTask {

  public final static String KEYWORD = "delete";

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  @CommandParam(pos = 0)
  @Required("Bitte gib eine Zahl an.")
  @Numeric(value = "Bitte gib eine Zahl zwischen 1 und 10 an.", min = 1, max = 100)
  private long numberOfMessagesToDelete;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    Flux<Message> messageIdsToDelete = this.messageCreateEventListener.getMessageChannel()
        .getMessagesBefore(this.getMessage().getId())
        .take(numberOfMessagesToDelete)
        .mergeWith(Flux.just(this.getMessage()));

    this.messageCreateEventListener.getMessageChannel()
        .bulkDeleteMessages(messageIdsToDelete)
        .blockLast();
  }
}
