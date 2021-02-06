package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.voice.VoiceConnection;

@Component
public class LeaveTask extends AbstractMessageTask implements IMessageTask {

  public final static String KEYWORD = "leave";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private SummonTask summonTask;

  @Override
  public void execute(Message message) {
    this.message = message;

    Optional<VoiceConnection> existingVoiceConnection = summonTask.getVoiceConnection();

    if (!existingVoiceConnection.isPresent()) {
      message.addReaction(ReactionEmoji.unicode("\u274c")).block();
      return;
    }

    existingVoiceConnection.get()
        .disconnect()
        .doOnSuccess((v) -> summonTask.setVoiceConnection(Optional.empty()))
        .block();
  }
}
