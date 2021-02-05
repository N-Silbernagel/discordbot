package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.voice.VoiceConnection;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

@Component
public class JoinTask extends AbstractMessageTask implements IMessageTask {

  public final static String KEYWORD = "join";

  @Getter
  @Setter
  private Optional<VoiceConnection> voiceConnection = Optional.empty();

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Autowired
  private LeaveTask leaveTask;

  @Override
  public void execute(Message message) {
    this.message = message;

    if (this.voiceConnection.isPresent()) {
      this.leaveTask.execute(message);
    }

    this.message.getAuthorAsMember()
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .flatMap(channel -> channel.join(spec -> spec.setProvider(lavaPlayerAudioProvider)))
        .flatMap(voiceConnection -> {
          this.voiceConnection = Optional.of(voiceConnection);
          return Mono.empty();
        })
        .block();
  }
}
