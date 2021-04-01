package com.github.nsilbernagel.discordbot.presence;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.discordjson.json.ActivityUpdateRequest;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PresenceManager {
  private final GatewayDiscordClient discordClient;

  @Getter
  private String currentlyPlaying;

  public PresenceManager(@Lazy GatewayDiscordClient discordClient){
    this.discordClient = discordClient;
  }

  public Mono<Void> online() {
    return this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online()
    );
  }

  public Mono<Void> trackPlaying(String title) {
    return this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online(
            setCurrentAndGenerateActivity(title)
        )
    );
  }

  public Mono<Void> trackPaused() {
    return this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online(
            Activity.playing(Emoji.PAUSE.getUnicodeEmoji() + this.getCurrentlyPlaying())
        )
    );
  }

  public Mono<Void> trackResumed() {
    return this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online(
            Activity.playing(this.getCurrentlyPlaying())
        )
    );
  }

  private ActivityUpdateRequest setCurrentAndGenerateActivity(String title){
    this.currentlyPlaying = title;
    return Activity.playing(title);
  }
}
