package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.message.MessageTask;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PongTask extends MessageTask<MsgTaskRequest> {
  public final static String KEYWORD = "ping";

  @CommandParam(pos = 0, range = Integer.MAX_VALUE)
  private List<String> commandParams;

  @Override
  public void action(MsgTaskRequest taskRequest) {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    taskRequest.respond("pong" + " " + String.join(" ", this.commandParams))
        .subscribe();
  }

  public boolean canHandle(String keyword) {
    return KEYWORD.equals(keyword);
  }
}