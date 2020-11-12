package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import discord4j.core.object.entity.Message;

public class Nils extends AbstractMessageTask implements IMessageTask {
    private final static String KEYWORD = "meinung";

    public Nils(Message message, CommandPattern pattern) {
        super(message, pattern);
    }

    @Override
    public void execute() {
        message.getChannel().flatMap(messageChannel -> messageChannel.createMessage("Richtiger Lappen.")).subscribe();
    }

    public static String getKeyword() {
        return KEYWORD;
    }
}
