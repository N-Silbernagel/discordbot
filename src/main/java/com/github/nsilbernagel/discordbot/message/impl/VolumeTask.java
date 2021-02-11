package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.FalseInputException;
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
  @Numeric("Bitte gib eine Zahl an.")
  private Integer volumeParam;

  @Override
  public void action() {

    if (this.volumeParam == null) {
      this.answerMessage("Aktuelle Lautstärke: " + this.lavaPlayerAudioProvider.getPlayer().getVolume() + "%").block();
      return;
    }

    if (this.volumeParam < 0 || this.volumeParam > 100) {
      throw new FalseInputException("Bitte gib eine Zahl zwischen 0 und 100 an.");
    }

    this.lavaPlayerAudioProvider.getPlayer().setVolume(this.volumeParam);
  }
}
