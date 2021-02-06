package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.FalseInputException;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

@Component
public class BulkDeleteTask extends AbstractMessageTask implements IMessageTask {

  public final static String KEYWORD = "delete";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private MessageToTaskHandler messageToTaskHandler;

  @Override
  public void execute(Message message) {
    this.message = message;

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

    if (messagesCount < 1 || messagesCount > 5) {
      throw new FalseInputException("Bitte gib eine Zahl zwischen 1 und 5 an.");
    }

    Mono<Void> bulkDeleteMono = this.message.getChannel()
        .flatMap(channel -> channel.getMessagesBefore(this.message.getId())
            .take(messagesCount)
            .flatMap(foundMessage -> foundMessage.delete())
            .then());

    Mono<Void> deleteThisMessageMono = this.message.delete();

    bulkDeleteMono.and(deleteThisMessageMono).block();
  }
}
