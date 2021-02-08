package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaTrackScheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NextTask extends AbstractMessageTask {

  public final static String KEYWORD = "next";

  @Autowired
  private LavaTrackScheduler lavaTrackScheduler;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    this.lavaTrackScheduler.nextTrack();
  }
}
