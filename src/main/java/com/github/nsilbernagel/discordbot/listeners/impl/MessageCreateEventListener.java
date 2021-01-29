package com.github.nsilbernagel.discordbot.listeners.impl;

import java.util.List;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import com.github.nsilbernagel.discordbot.message.TaskLogicException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;

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
    Message message = event.getMessage();
    List<IMessageTask> tasks = messageToTaskHandler.getMessageTasks(message);

    tasks.forEach(task -> {
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