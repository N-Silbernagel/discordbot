package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.listeners.impl.MessageCreateEventListener;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.VoiceState;
import discord4j.voice.VoiceConnection;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
public class SummonTask extends MessageTask implements ExplainedMessageTask {

  public final static String KEYWORD = "summon";

  @Getter
  @Setter
  private Optional<VoiceConnection> voiceConnection = Optional.empty();

  public boolean canHandle(String keyword) {
    return keyword.equals(KEYWORD) || keyword.equals("join");
  }

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Autowired
  private MessageCreateEventListener messageCreateEventListener;

  @Autowired
  private LeaveTask leaveTask;

  @Override
  public void action() {
    if (this.voiceConnection.isPresent()) {
      this.leaveTask.execute();
    }

    this.voiceConnection = Optional.ofNullable(this.messageCreateEventListener.getMsgAuthor()
        .getVoiceState()
        .doOnError(TimeoutException.class, (error) -> {
          throw new TaskException("Leider ist ein Fehler aufgetreten", error);
        })
        .flatMap(VoiceState::getChannel)
        .flatMap(channel -> channel.join(spec -> spec.setProvider(lavaPlayerAudioProvider)))
        .block());

    // user is not in a voice channel ->
    if(this.voiceConnection.isEmpty()) {
      throw new TaskException("Bitte join einem Channel, damit ich hinterher kann.");
    }
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Den Bot in den Sprachchannel beschwören.";
  }
}
