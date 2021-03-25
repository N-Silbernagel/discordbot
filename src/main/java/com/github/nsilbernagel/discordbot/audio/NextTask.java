package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.audio.LavaTrackScheduler;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NextTask extends MessageTask implements ExplainedMessageTask {

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

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Das nächste Lied abspielen.";
  }
}
