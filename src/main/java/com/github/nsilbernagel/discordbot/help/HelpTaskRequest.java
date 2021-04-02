package com.github.nsilbernagel.discordbot.help;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.task.validation.Validator;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.Getter;

public class HelpTaskRequest extends MsgTaskRequest {
  @CommandParam(pos = 0)
  @Getter
  private String taskToExplainQuery;

  public HelpTaskRequest(Message message, TextChannel channel, Member member, String commandToken, Validator<MsgTaskRequest> validator) {
    super(
        message,
        channel,
        member,
        commandToken,
        validator
    );
  }
}
