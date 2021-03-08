package com.github.nsilbernagel.discordbot.message.impl;

import java.util.List;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.audio.Sound;
import com.github.nsilbernagel.discordbot.audio.SoundsSource;
import com.github.nsilbernagel.discordbot.audio.awsmsounds.dto.AwsmSound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoundTask extends AbstractMessageTask implements ExplainedMessageTask {
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
  public void action() {
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

    playSound(soundToPlay.get());
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Einen Sound abspielen";
  }

  public void playSound(Sound sound) {
    playTask.setAudioSourceString(sound.getSource());
    playTask.execute();
  }
}
