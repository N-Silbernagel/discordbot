package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.validation.MessageTaskValidator;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.mockito.Mockito;

public final class MessageTestUtil {
  public static MsgTaskRequest generateMsgTaskRequest() {
    return new MsgTaskRequest(
        Mockito.mock(Message.class),
        Mockito.mock(TextChannel.class),
        Mockito.mock(Member.class),
        "!",
        Mockito.mock(MessageTaskValidator.class)
    );
  }
}
