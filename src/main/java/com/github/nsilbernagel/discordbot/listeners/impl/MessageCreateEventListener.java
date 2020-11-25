package com.github.nsilbernagel.discordbot.listeners.impl;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import discord4j.core.event.domain.message.MessageCreateEvent;

public class MessageCreateEventListener implements EventListener<MessageCreateEvent> {
    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        MessageToTaskHandler.getMessageTask(event.getMessage()).ifPresent(IMessageTask::execute);
    }
}