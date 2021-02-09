package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.audio.LavaPlayerAudioProvider;
import com.github.nsilbernagel.discordbot.message.AbstractMessageTask;
import com.github.nsilbernagel.discordbot.message.FalseInputException;

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

  @Override
  public void action() {

    if (this.messageToTaskHandler.getCommandParameters().size() == 0) {
      this.answerMessage("Aktuelle Lautstärke: " + this.lavaPlayerAudioProvider.getPlayer().getVolume() + "%").block();
      return;
    }

    String newVolumeString = this.messageToTaskHandler.getCommandParameters().get(0);

    Integer newVolume;

    try {
      newVolume = Integer.parseInt(newVolumeString);
    } catch (NumberFormatException e) {
      throw new FalseInputException("Bitte gib eine Zahl an.");
    }

    if (newVolume < 0 || newVolume > 100) {
      throw new FalseInputException("Bitte gib eine Zahl zwischen 0 und 100 an.");
    }

    this.lavaPlayerAudioProvider.getPlayer().setVolume(newVolume);
  }
}
