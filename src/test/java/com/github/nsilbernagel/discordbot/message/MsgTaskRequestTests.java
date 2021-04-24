package com.github.nsilbernagel.discordbot.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MsgTaskRequestTests {
  @Spy
  private final MsgTaskRequest msgTaskRequest = MessageTestUtil.generateMsgTaskRequest();

  @Test
  public void it_calculates_the_correct_command(){
    String testMessageContent = msgTaskRequest.getCommandToken() + "abc test 1 2 3";
    when(this.msgTaskRequest.getMessage().getContent()).thenReturn(testMessageContent);

    assertEquals("abc", this.msgTaskRequest.getCommand());
  }

  @Test
  public void it_returns_all_command_params_of_a_message() {
    String testMessageContent = "!abc test 1 2 3";
    when(this.msgTaskRequest.getMessage().getContent()).thenReturn(testMessageContent);

    List<String> expectedCommandParams = List.of("test", "1", "2", "3");

    assertEquals(expectedCommandParams, msgTaskRequest.getCommandParameters());
  }
}
