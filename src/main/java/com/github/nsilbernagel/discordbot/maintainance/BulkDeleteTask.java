package com.github.nsilbernagel.discordbot.maintainance;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric;
import com.github.nsilbernagel.discordbot.message.validation.annotations.Required;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;
import reactor.core.publisher.Flux;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class BulkDeleteTask extends MessageTask<MsgTaskRequest> {

  public final static String KEYWORD = "delete";

  @CommandParam(pos = 0)
  @Required("Bitte gib eine Zahl an.")
  @Numeric(value = "Bitte gib eine Zahl zwischen 1 und 10 an.", min = 1, max = 100)
  private long numberOfMessagesToDelete;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    Flux<Message> messageIdsToDelete = taskRequest.getChannel()
        .getMessagesBefore(taskRequest.getMessage().getId())
        .take(numberOfMessagesToDelete)
        .mergeWith(Flux.just(taskRequest.getMessage()));

    taskRequest.getChannel()
        .bulkDeleteMessages(messageIdsToDelete)
        .blockLast();
  }
}
