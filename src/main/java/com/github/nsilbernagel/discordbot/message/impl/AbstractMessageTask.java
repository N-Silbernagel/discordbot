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
}
