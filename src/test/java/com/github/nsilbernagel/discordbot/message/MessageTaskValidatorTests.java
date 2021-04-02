package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import com.github.nsilbernagel.discordbot.message.validation.MessageTaskValidator;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageTaskValidatorTests {
  @Spy
  private final MsgTaskRequest msgTaskRequest = MessageTestUtil.generateMsgTaskRequest();
  @Spy
  private final MessageTaskValidator messageTaskValidator = new MessageTaskValidator();

  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void it_acknowledges_a_valid_request() {
    assertEquals("", this.messageTaskValidator.validate(this.msgTaskRequest));
  }

  @Test
  public void it_assigns_fields_annotated_as_commandParam() {
    CommandParamRequestStub commandParamRequestStub = spy(new CommandParamRequestStub());

    String commandParamExpectedValue = "param";
    String messageContentWithCommandParam = commandParamRequestStub.getCommandToken() + "abc " + commandParamExpectedValue;

    when(commandParamRequestStub.getMessage().getContent()).thenReturn(messageContentWithCommandParam);

    this.messageTaskValidator.validate(commandParamRequestStub);

    assertEquals(commandParamExpectedValue, commandParamRequestStub.x);
  }
}
