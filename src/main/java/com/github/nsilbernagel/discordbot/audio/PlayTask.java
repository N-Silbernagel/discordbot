package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;
import com.github.nsilbernagel.discordbot.validation.CommandParam;
import com.github.nsilbernagel.discordbot.validation.rules.annotations.Required;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayTask extends MessageTask implements ExplainedMessageTask {
  public final static String KEYWORD = "play";

  @Autowired
  private SummonTask summonTask;

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Autowired
  private LavaResultHandler lavaResultHandler;

  @CommandParam(pos = 0)
  @Required("Bitte gib einen Link zu einer Audioquelle an.")
  private String audioSourceString;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action() {

    if (summonTask.getVoiceConnection().isEmpty()) {
      summonTask.execute();
    }

    this.loadAudioSource(this.audioSourceString);
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Eine Audioquelle in die Warteschlange packen.";
  }

  public void loadAudioSource(String audioSource) throws LavaPlayerException {
    lavaPlayerAudioProvider.getPlayerManager()
      .loadItem(audioSource, this.lavaResultHandler);
  }

  public void setAudioSourceString(String src) {
    this.audioSourceString = src;
  }
}
