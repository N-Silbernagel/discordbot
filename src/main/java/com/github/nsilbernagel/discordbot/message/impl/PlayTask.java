package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.audio.LavaPlayerException;
import com.github.nsilbernagel.discordbot.audio.LavaResultHandler;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskException;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayTask extends AbstractMessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "play";

  @Autowired
  private SummonTask summonTask;

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Autowired
  private LavaResultHandler lavaResultHandler;

  @CommandParam(pos = 0)
  @Required("Bitte gib einen Link zu einer Audioquelle an.")
  private String audioSourceString;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {

    if (!summonTask.getVoiceConnection().isPresent()) {
      summonTask.execute();
    }

    try {
      lavaPlayerAudioProvider.getPlayerManager()
          .loadItem(this.audioSourceString, this.lavaResultHandler);
    } catch (LavaPlayerException e) {
      throw new TaskException(e.getMessage());
    }
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Eine Audioquelle in die Warteschlange packen.";
  }

  public void setAudioSourceString(String src) {
    this.audioSourceString = src;
  }
}
