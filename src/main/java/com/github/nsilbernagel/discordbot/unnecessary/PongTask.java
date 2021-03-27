package com.github.nsilbernagel.discordbot.unnecessary;

import com.github.nsilbernagel.discordbot.message.MessageTask;

import org.springframework.stereotype.Component;

@Component
public class PongTask extends MessageTask {
  public final static String KEYWORD = "ping";

  @Override
  public void action() {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    this.answerMessage("pong" + this.currentMessage().getContent())
        .subscribe();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}