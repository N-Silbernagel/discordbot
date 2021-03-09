package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.voice.VoiceConnection;

@Component
public class LeaveTask extends AbstractMessageTask implements ExplainedMessageTask {

  public final static String KEYWORD = "leave";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private SummonTask summonTask;

  @Override
  public void action() {
    Optional<VoiceConnection> existingVoiceConnection = summonTask.getVoiceConnection();

    if (existingVoiceConnection.isEmpty()) {
      this.getMessage().addReaction(ReactionEmoji.unicode("❌")).block();
      return;
    }

    existingVoiceConnection.get()
        .disconnect()
        .doOnSuccess((v) -> summonTask.setVoiceConnection(Optional.empty()))
        .block();
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Dem Bot befehlen den Sprachchannel zu verlassen.";
  }
}
