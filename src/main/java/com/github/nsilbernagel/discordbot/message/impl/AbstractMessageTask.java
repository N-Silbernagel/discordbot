package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.CommandPattern;

import discord4j.core.object.entity.Message;

abstract class AbstractMessageTask {
    final Message message;
    final CommandPattern commandPattern;

    public AbstractMessageTask(Message message, CommandPattern pattern) {
        this.message = message;
        this.commandPattern = pattern;
    }

    /**
     * Answer the message with a given text on the same channel
     *
     * @param answerText
     */
    public void answerMessage(String answerText) {
        message.getChannel().flatMap(messageChannel -> messageChannel.createMessage(answerText)).subscribe();
    }
}
