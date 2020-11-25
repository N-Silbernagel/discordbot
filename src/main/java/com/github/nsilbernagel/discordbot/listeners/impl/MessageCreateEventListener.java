package com.github.nsilbernagel.discordbot.listeners.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.MessageToTaskHandler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.reaction.ReactionEmoji;

public class MessageCreateEventListener implements EventListener<MessageCreateEvent> {
    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public void execute(MessageCreateEvent event) {
        Optional<IMessageTask> msgTask = MessageToTaskHandler.getMessageTask(event.getMessage());
        if (msgTask.isPresent()) {
            // execute task if it exists
            msgTask.get().execute();
        } else {
            // react with question mark if it doesnt
            event.getMessage().addReaction(ReactionEmoji.unicode("\u2753")).block();
        }
    }
}