package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.message.validation.MessageTaskValidator;
import com.github.nsilbernagel.discordbot.message.validation.annotations.Numeric;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.mockito.Mockito;

public class NumericParamRequestStub extends MsgTaskRequest{
  @CommandParam(pos = 0)
  @Numeric("abc")
  public String x;

  public NumericParamRequestStub(){
    super(
        Mockito.mock(Message.class),
        Mockito.mock(TextChannel.class),
        Mockito.mock(Member.class),
        "!",
        Mockito.mock(MessageTaskValidator.class)
    );
  }
}
