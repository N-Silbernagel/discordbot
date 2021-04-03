package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.message.MessageTask;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import org.springframework.stereotype.Component;

@Component
public class PongTask extends MessageTask {
  public final static String KEYWORD = "ping";

  @Override
  public void action(MsgTaskRequest taskRequest) {
    String commandParams = taskRequest.param(0, Integer.MAX_VALUE)
        .as(String.class);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    taskRequest.respond("pong" + " " + commandParams)
        .subscribe();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}