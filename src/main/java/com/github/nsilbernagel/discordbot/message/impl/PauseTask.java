package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PauseTask extends AbstractMessageTask {

  public final static String KEYWORD = "pause";

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    this.lavaPlayerAudioProvider.getPlayer().setPaused(true);
  }
}