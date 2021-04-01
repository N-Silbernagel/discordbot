package com.github.nsilbernagel.discordbot.presence;

import com.github.nsilbernagel.discordbot.reaction.Emoji;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public final class PresenceManager {
  private final GatewayDiscordClient discordClient;

  @Getter
  private String currentlyPlaying;

  public PresenceManager(@Lazy GatewayDiscordClient discordClient){
    this.discordClient = discordClient;
  }

  public void online() {
    this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online()
    )
        .block();
  }

  public void trackPlaying(String title) {
    this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online(
            Activity.playing(title)
        )
    )
        .block();

    this.currentlyPlaying = title;
  }

  public void trackPaused() {
    this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online(
            Activity.playing(Emoji.PAUSE.getUnicodeEmoji() + this.getCurrentlyPlaying())
        )
    )
        .block();
  }

  public void trackResumed() {
    this.discordClient.updatePresence(
        discord4j.core.object.presence.Presence.online(
            Activity.playing(this.getCurrentlyPlaying())
        )
    )
        .block();
  }
}
