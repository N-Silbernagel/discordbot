package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.message.validation.MessageTaskValidator;
import com.github.nsilbernagel.discordbot.message.validation.MessageValidationException;
import com.github.nsilbernagel.discordbot.message.validation.Numeric;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageTaskValidatorTests {
  @Spy
  private final MsgTaskRequest msgTaskRequest = MessageTestUtil.generateMsgTaskRequest();
  @Spy
  private final MessageTaskValidator messageTaskValidator = new MessageTaskValidator(Collections.singletonList(new Numeric()));

  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void it_acknowledges_a_valid_request() {
    assertTrue(this.messageTaskValidator.validate(this.msgTaskRequest));
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

  @Test
  public void it_validates_fields_annotated_with_command_param(){
    NumericParamRequestStub commandParamRequestStub = spy(new NumericParamRequestStub());

    // fake that message has non numeric command param (xyz)
    String messageContentWithCommandParam = commandParamRequestStub.getCommandToken() + "abc xyz";

    when(commandParamRequestStub.getMessage().getContent()).thenReturn(messageContentWithCommandParam);

    assertThrows(MessageValidationException.class, () -> {
      this.messageTaskValidator.validate(commandParamRequestStub);
    });
  }
}
