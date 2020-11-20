package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.impl.NilsTask;
import com.github.nsilbernagel.discordbot.message.impl.PongTask;
import com.github.nsilbernagel.discordbot.message.impl.RandomNumberGeneratorTask;
import com.github.nsilbernagel.discordbot.message.impl.VoteKickTask;

import discord4j.core.object.entity.Message;

public enum EMessageToTaskMapper {

    PONG(PongTask.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new PongTask(message, commandPattern);
        }
    },

    NILS(NilsTask.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new NilsTask(message, commandPattern);
        }
    },

    DICE(RandomNumberGeneratorTask.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new RandomNumberGeneratorTask(message, commandPattern);
        }
    },

    VOTEKICK(VoteKickTask.getKeyword()) {
        @Override
        public IMessageTask getTask(Message message, CommandPattern commandPattern) {
            return new VoteKickTask(message, commandPattern);
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