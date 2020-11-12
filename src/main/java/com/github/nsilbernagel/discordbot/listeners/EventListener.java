package com.github.nsilbernagel.discordbot.listeners;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {
    Class<T> getEventType();
    void execute(T event);
}
