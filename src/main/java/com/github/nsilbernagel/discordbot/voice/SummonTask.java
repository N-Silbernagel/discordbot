package com.github.nsilbernagel.discordbot.voice;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.task.TaskException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import discord4j.core.object.VoiceState;
import discord4j.voice.VoiceConnection;
import lombok.Getter;
import lombok.Setter;

@Component
public class SummonTask extends MessageTask implements ExplainedMessageTask {

  public final static String KEYWORD = "summon";

  private final LavaPlayerAudioProvider lavaPlayerAudioProvider;

  private final LeaveTask leaveTask;

  @Getter
  @Setter
  private Optional<VoiceConnection> voiceConnection = Optional.empty();

  public SummonTask(LavaPlayerAudioProvider lavaPlayerAudioProvider, @Lazy LeaveTask leaveTask) {
    this.lavaPlayerAudioProvider = lavaPlayerAudioProvider;
    this.leaveTask = leaveTask;
  }

  public boolean canHandle(String keyword) {
    return keyword.equals(KEYWORD) || keyword.equals("join");
  }

  @Override
  public void action() {
    if (this.voiceConnection.isPresent()) {
      this.leaveTask.execute(this.msgTaskRequest.get());
    }

    this.voiceConnection = Optional.ofNullable(
        this.currentAuthor()
            .getVoiceState()
            .flatMap(VoiceState::getChannel)
            .flatMap(channel -> channel.join(spec -> spec.setProvider(lavaPlayerAudioProvider)))
            .block()
        );

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
