package com.github.nsilbernagel.discordbot.schedules.dto;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

@Component
public class ChannelCleaner {

    private GatewayDiscordClient discordClient;

    private MessageChannel channel;

    public ChannelCleaner setChannel(Snowflake channelId) {
        this.channel = (MessageChannel) this.discordClient.getChannelById(channelId).block();
        return this;
    }

    public ChannelCleaner setDiscordClient(GatewayDiscordClient discordClient) {
        this.discordClient = discordClient;
        return this;
    }

    public void removeMessages() {
        List<Message> messages = channel.getMessagesBefore(Snowflake.of(Instant.now()))
                                        .take(50)
                                        .collectList()
                                        .block();

        for (Message message : messages) {
            message.getAuthor().ifPresent(act -> {
                if (act.isBot() || message.getContent().startsWith("!")) {
                    message.delete().subscribe();
                }
            });
        }
    }

}
