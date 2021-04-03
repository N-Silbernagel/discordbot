package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;

import com.github.nsilbernagel.discordbot.message.validation.rules.Numeric;
import org.springframework.stereotype.Component;

@Component
public class VolumeTask extends MessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "volume";

  private final LavaPlayerAudioProvider lavaPlayerAudioProvider;

  public VolumeTask(LavaPlayerAudioProvider lavaPlayerAudioProvider) {
    this.lavaPlayerAudioProvider = lavaPlayerAudioProvider;
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  protected void action(MsgTaskRequest taskRequest) {
    Integer newVolume = taskRequest.param(0)
        .is(new Numeric(), "Bitte gib eine Zahl zwischen 0 und 100 an.")
        .as(Integer.class);

    if (newVolume == null) {
      taskRequest.respond("Aktuelle Lautstärke: " + this.lavaPlayerAudioProvider.getPlayer().getVolume() + "%").block();
      return;
    }

    this.lavaPlayerAudioProvider.getPlayer().setVolume(newVolume);
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Die Lautstärke des Bot anpassen.";
  }
}
