package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.message.validation.MessageTaskValidator;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.mockito.Mockito;

public class CommandParamRequestStub extends MsgTaskRequest{
  @CommandParam(pos = 0)
  public String x;

  public CommandParamRequestStub(){
    super(
        Mockito.mock(Message.class),
        Mockito.mock(TextChannel.class),
        Mockito.mock(Member.class),
        "!",
        Mockito.mock(MessageTaskValidator.class)
    );
  }
}
