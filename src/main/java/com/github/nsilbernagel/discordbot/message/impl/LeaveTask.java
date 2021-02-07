package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.voice.VoiceConnection;

@Component
public class LeaveTask extends AbstractMessageTask {

  public final static String KEYWORD = "leave";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private SummonTask summonTask;

  @Override
  public void action() {
    Optional<VoiceConnection> existingVoiceConnection = summonTask.getVoiceConnection();

    if (!existingVoiceConnection.isPresent()) {
      this.getMessage().addReaction(ReactionEmoji.unicode("\u274c")).block();
      return;
    }

    existingVoiceConnection.get()
        .disconnect()
        .doOnSuccess((v) -> summonTask.setVoiceConnection(Optional.empty()))
        .block();
  }
}
