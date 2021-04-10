package com.github.nsilbernagel.discordbot.audio;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;
import com.github.nsilbernagel.discordbot.audio.awsmsounds.dto.AwsmSound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoundTask extends MessageTask implements ExplainedMessageTask {
  public static final String KEYWORD = "sound";

  @Autowired
  private PlayTask playTask;

  @Autowired
  private SoundsSource<AwsmSound> soundsSource;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    String soundQuery = taskRequest.param(0, Integer.MAX_VALUE)
        .as(String.class);

    Optional<? extends Sound> soundToPlay;

    if (soundQuery == null) {
      soundToPlay = Optional.of(this.soundsSource.random());
    } else {
      soundToPlay = this.soundsSource.filter(soundQuery);
    }

    if (soundToPlay.isEmpty()) {
      throw new TaskException("Sound konnte nicht gefunden werden.");
    }

    this.playTask.connectToVoice(taskRequest);
    this.playTask.loadAudioSource(soundToPlay.get().getSource(), taskRequest);
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Einen Sound abspielen";
  }
}
