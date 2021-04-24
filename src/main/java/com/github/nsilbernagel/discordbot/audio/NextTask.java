package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MessageCreateTask;
import com.github.nsilbernagel.discordbot.message.ExplainedMessageTask;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NextTask extends MessageCreateTask implements ExplainedMessageTask {

  public final static String KEYWORD = "next";

  @Autowired
  private LavaTrackScheduler lavaTrackScheduler;

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }

  @Override
  public void action(MsgTaskRequest taskRequest) {
    this.lavaTrackScheduler.nextTrack();
  }

  public String getKeyword() {
    return KEYWORD;
  }

  public String getExplaination() {
    return "Das nächste Lied abspielen.";
  }
}
