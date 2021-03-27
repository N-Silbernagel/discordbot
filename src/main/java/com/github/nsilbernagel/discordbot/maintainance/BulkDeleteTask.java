package com.github.nsilbernagel.discordbot.maintainance;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;
import reactor.core.publisher.Flux;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class BulkDeleteTask extends MessageTask {

  public final static String KEYWORD = "delete";

  @CommandParam(pos = 0)
  @Required("Bitte gib eine Zahl an.")
  @Numeric(value = "Bitte gib eine Zahl zwischen 1 und 10 an.", min = 1, max = 100)
  private long numberOfMessagesToDelete;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    Flux<Message> messageIdsToDelete = this.currentChannel()
        .getMessagesBefore(this.currentMessage().getId())
        .take(numberOfMessagesToDelete)
        .mergeWith(Flux.just(this.currentMessage()));

    this.currentChannel()
        .bulkDeleteMessages(messageIdsToDelete)
        .blockLast();
  }
}
