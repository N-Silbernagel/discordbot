package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskRequest;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

import com.github.nsilbernagel.discordbot.voice.SummonTask;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class PlayTask extends MessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "play";

  private final SummonTask summonTask;

  private final LavaPlayerAudioProvider lavaPlayerAudioProvider;

  private final LavaTrackScheduler lavaTrackScheduler;

  @CommandParam(pos = 0)
  @Required("Bitte gib einen Link zu einer Audioquelle an.")
  private String audioSourceString;

  public PlayTask(@Lazy SummonTask summonTask, @Lazy LavaPlayerAudioProvider lavaPlayerAudioProvider, @Lazy LavaTrackScheduler lavaTrackScheduler) {
    this.summonTask = summonTask;
    this.lavaPlayerAudioProvider = lavaPlayerAudioProvider;
    this.lavaTrackScheduler = lavaTrackScheduler;
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {

    this.connectToVoice(this.msgTaskRequest.get());

    this.loadAudioSource(this.audioSourceString, this.msgTaskRequest.get());
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Eine Audioquelle in die Warteschlange packen.";
  }

  public void connectToVoice(TaskRequest msgTaskRequest) {
    if (summonTask.getVoiceConnection().isEmpty()) {
      summonTask.execute(msgTaskRequest);
    }
  }

  public void loadAudioSource(String audioSource, TaskRequest taskRequest) {
    AudioRequest audioRequest = new AudioRequest(
        audioSource,
        taskRequest
    );

    this.lavaTrackScheduler.getAudioRequest()
        .put(audioSource, audioRequest);

    try {
      lavaPlayerAudioProvider.getPlayerManager()
        .loadItem(audioSource, new LavaResultHandler(this.lavaTrackScheduler, audioSource))
        .get();
    } catch (InterruptedException | ExecutionException ignored) {
    }

    if (audioRequest.getStatus().isDeletable()) {
      taskRequest.getChannel()
          .createMessage("Auf <" + audioSource + "> konnte keine Audioquelle gefunden werden.")
          .block();
      this.lavaTrackScheduler.getAudioRequest().remove(audioSource);
    }
  }
}
