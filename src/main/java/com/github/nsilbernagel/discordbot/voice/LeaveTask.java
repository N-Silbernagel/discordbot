package com.github.nsilbernagel.discordbot.voice;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.reaction.Emoji;
import com.github.nsilbernagel.discordbot.message.MessageCreateTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.voice.VoiceConnection;

@Component
public class LeaveTask extends MessageCreateTask implements ExplainedMessageTask {

  public final static String KEYWORD = "leave";

  public boolean canHandle(String keyword) {
    return keyword.equals(KEYWORD) || keyword.equals("disconnect");
  }

  @Autowired
  private SummonTask summonTask;

  @Override
  public void action(MsgTaskRequest taskRequest) {
    Optional<VoiceConnection> existingVoiceConnection = summonTask.getVoiceConnection();

    if (existingVoiceConnection.isEmpty()) {
      Emoji.CROSS.reactOn(taskRequest.getMessage()).block();
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
