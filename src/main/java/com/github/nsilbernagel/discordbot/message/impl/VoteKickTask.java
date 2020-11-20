package com.github.nsilbernagel.discordbot.message.impl;

import java.util.HashMap;
import java.util.List;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class VoteKickTask extends AbstractMessageTask implements IMessageTask {
    private final static String KEYWORD = "votekick";

    private HashMap<String, List<User>> userVotings = new HashMap<String, List<User>>();

    public VoteKickTask(Message message, CommandPattern pattern) {
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