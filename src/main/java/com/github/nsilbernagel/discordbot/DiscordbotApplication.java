package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.listeners.EventListener;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscordbotApplication {

  public static void main(String[] args) {
    Dotenv env = Dotenv.load();

    DiscordClient dClient = DiscordClient.create(env.get("DISCORD_TOKEN"));
    GatewayDiscordClient client = dClient.login().block();
    EventListener<MessageCreateEvent> listener = new MessageCreateEventListener();
    client.on(listener.getEventType()).subscribe(listener::execute);

    client.onDisconnect().block();

    SpringApplication.run(DiscordbotApplication.class, args);
  }

}
