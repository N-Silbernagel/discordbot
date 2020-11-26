package com.github.nsilbernagel.discordbot.listeners.impl;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskLogicException;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class MessageCreateEventListener implements EventListener<MessageCreateEvent> {
  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {
    MessageToTaskHandler.getMessageTask(event.getMessage()).ifPresent(task -> {
      try {
        task.execute();
      } catch (TaskLogicException taskLogicError) {
        if (taskLogicError.hasMessage()) {
          event.getMessage().getChannel()
              .flatMap(channel -> channel.createMessage(taskLogicError.getMessage())).block();
        }
      }
    });
  }
}