package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.audio.LavaPlayerException;
import com.github.nsilbernagel.discordbot.audio.LavaResultHandler;
import com.github.nsilbernagel.discordbot.message.TaskException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayTask extends AbstractMessageTask {
  public final static String KEYWORD = "play";

  @Autowired
  private SummonTask summonTask;

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Autowired
  private LavaResultHandler lavaResultHandler;

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
          .loadItem(
              messageToTaskHandler.getCommandParameters()
                  .get(0),
              this.lavaResultHandler);
    } catch (LavaPlayerException e) {
      throw new TaskException(e.getMessage());
    }
  }
}
