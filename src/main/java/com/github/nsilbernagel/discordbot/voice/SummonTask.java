package com.github.nsilbernagel.discordbot.voice;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.MessageCreateTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import discord4j.core.object.VoiceState;
import discord4j.voice.VoiceConnection;
import lombok.Getter;
import lombok.Setter;

@Component
public class SummonTask extends MessageCreateTask implements ExplainedMessageTask {

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
    return keyword.equals(KEYWORD) || keyword.equals("join") || keyword.equals("connect");
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    if (this.voiceConnection.isPresent()) {
      this.leaveTask.execute(taskRequest);
    }

    this.voiceConnection = Optional.ofNullable(
        taskRequest.getAuthor()
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
