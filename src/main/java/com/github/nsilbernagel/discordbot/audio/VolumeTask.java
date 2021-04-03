package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric;

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

  @CommandParam(pos = 0)
  @Numeric(value = "Bitte gib eine Zahl zwischen 0 und 100 an.", min = 0, max = 100)
  private Integer volumeParam;

  @Override
  protected void action(MsgTaskRequest taskRequest) {

    if (this.volumeParam == null) {
      taskRequest.respond("Aktuelle Lautstärke: " + this.lavaPlayerAudioProvider.getPlayer().getVolume() + "%").block();
      return;
    }

    this.lavaPlayerAudioProvider.getPlayer().setVolume(this.volumeParam);
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Die Lautstärke des Bot anpassen.";
  }
}
