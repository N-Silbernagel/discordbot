package com.github.nsilbernagel.discordbot.listeners.impl;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskLogicException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;

@Component
public class MessageCreateEventListener implements EventListener<MessageCreateEvent> {
  @Autowired
  private MessageToTaskHandler messageToTaskHandler;

  @Override
  public Class<MessageCreateEvent> getEventType() {
    return MessageCreateEvent.class;
  }

  @Override
  public void execute(MessageCreateEvent event) {
    messageToTaskHandler.getMessageTask(event.getMessage()).ifPresent(task -> {
      try {
        task.execute(event.getMessage());
      } catch (TaskLogicException taskLogicError) {
        if (taskLogicError.hasMessage()) {
          event.getMessage().getChannel()
              .flatMap(channel -> channel.createMessage(taskLogicError.getMessage())).block();
        }
      }
    });
  }
}