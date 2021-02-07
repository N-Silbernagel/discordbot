package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.audio.TrackScheduler;
import com.github.nsilbernagel.discordbot.message.IMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayTask extends AbstractMessageTask implements IMessageTask {
  public final static String KEYWORD = "play";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private SummonTask summonTask;

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Override
  public void execute() {

    if (!summonTask.getVoiceConnection().isPresent()) {
      summonTask.execute();
    }

    final TrackScheduler scheduler = new TrackScheduler(lavaPlayerAudioProvider.getPlayer());

    lavaPlayerAudioProvider.getPlayerManager()
        .loadItem(messageToTaskHandler.getCommandParameters().get(0), scheduler);
  }
}
