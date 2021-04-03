package com.github.nsilbernagel.discordbot.maintainance;

import com.github.nsilbernagel.discordbot.guard.annotations.NeedsPermission;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.rules.Max;
import com.github.nsilbernagel.discordbot.message.validation.rules.Min;
import com.github.nsilbernagel.discordbot.message.validation.rules.Numeric;
import com.github.nsilbernagel.discordbot.message.validation.rules.Required;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;

import discord4j.rest.util.Permission;
import reactor.core.publisher.Flux;

@Component
@NeedsPermission(Permission.ADMINISTRATOR)
public class BulkDeleteTask extends MessageTask {

  public final static String KEYWORD = "delete";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    Long numberOfMessagesToDelete = taskRequest.param(0)
        .is(new Required(), "Bitte gib an, wie viele Nachrichten du löschen möchtest.")
        .is(new Numeric(), "Bitte gib eine Zahl an.")
        .is(new Min(1), "Bitte gib eine Zahl über 0 an.")
        .is(new Max(100), "Bitte gib eine Zahl unter 101 an")
        .as(Long.class);

    Flux<Message> messageIdsToDelete = taskRequest.getChannel()
        .getMessagesBefore(taskRequest.getMessage().getId())
        .take(numberOfMessagesToDelete)
        .mergeWith(Flux.just(taskRequest.getMessage()));

    taskRequest.getChannel()
        .bulkDeleteMessages(messageIdsToDelete)
        .blockLast();
  }
}
