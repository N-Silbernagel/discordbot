package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Numeric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VolumeTask extends AbstractMessageTask {
  public final static String KEYWORD = "volume";

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @CommandParam(0)
  @Numeric(value = "Bitte gib eine Zahl zwischen 0 und 100 an.", min = 0, max = 100)
  private Integer volumeParam;

  @Override
  public void action() {

    if (this.volumeParam == null) {
      this.answerMessage("Aktuelle Lautstärke: " + this.lavaPlayerAudioProvider.getPlayer().getVolume() + "%").block();
      return;
    }

    this.lavaPlayerAudioProvider.getPlayer().setVolume(this.volumeParam);
  }
}
