package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PauseTask extends MessageTask implements ExplainedMessageTask {

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

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Das Abspielen eines Lieds abbrechen.";
  }
}
