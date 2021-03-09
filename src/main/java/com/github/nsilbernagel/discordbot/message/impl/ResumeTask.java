package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResumeTask extends MessageTask implements ExplainedMessageTask {

  public final static String KEYWORD = "resume";

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {
    this.lavaPlayerAudioProvider.getPlayer().setPaused(false);
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Musik abspielen fortsetzen.";
  }
}
