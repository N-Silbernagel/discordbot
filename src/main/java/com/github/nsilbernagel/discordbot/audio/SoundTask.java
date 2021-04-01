package com.github.nsilbernagel.discordbot.audio;

import java.util.List;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.audio.awsmsounds.dto.AwsmSound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoundTask extends MessageTask implements ExplainedMessageTask {
  public static final String KEYWORD = "sound";

  @CommandParam(pos = 0, range = Integer.MAX_VALUE)
  private List<String> soundQueryList;

  @Autowired
  private PlayTask playTask;

  @Autowired
  private SoundsSource<AwsmSound> soundsSource;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    Optional<? extends Sound> soundToPlay;

    String soundQuery = String.join(" ", this.soundQueryList);

    if (soundQuery.isEmpty()) {
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
