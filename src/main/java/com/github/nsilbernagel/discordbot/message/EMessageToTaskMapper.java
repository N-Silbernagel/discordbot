package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.impl.Nils;
import com.github.nsilbernagel.discordbot.message.impl.PongTask;
import com.github.nsilbernagel.discordbot.message.impl.RandomNumberGeneratorTask;
import discord4j.core.object.entity.Message;

public enum EMessageToTaskMapper {

    PONG (PongTask.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new PongTask(message, commandPattern);
        }
    },

    NILS (Nils.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new Nils(message, commandPattern);
        }
    },

    DICE (RandomNumberGeneratorTask.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new RandomNumberGeneratorTask(message, commandPattern);
        }
    };

    private final String messageKey; // Keyword that shall map the command to the write IMessageTask.

    EMessageToTaskMapper(String messageKey) {
        this.messageKey = messageKey;
    }

    public abstract IMessageTask getTask(Message message, CommandPattern commandPattern);

    public String getMessageKey() {
        return messageKey;
    }
}