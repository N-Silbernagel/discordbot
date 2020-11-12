package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import discord4j.core.object.entity.Message;

public class PongTask extends AbstractMessageTask implements IMessageTask {
    private final static String KEYWORD = "ping";

    public PongTask(Message message, CommandPattern pattern) {
        super(message, pattern);
    }

    @Override
    public void execute() {
        message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("pong")).subscribe();
    }

    public static String getKeyword() {
        return KEYWORD;
    }
}