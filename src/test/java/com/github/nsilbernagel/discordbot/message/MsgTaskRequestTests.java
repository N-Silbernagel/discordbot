package com.github.nsilbernagel.discordbot.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MsgTaskRequestTests {
  @Spy
  private final MsgTaskRequest msgTaskRequest = MessageTestUtil.generateMsgTaskRequest();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

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

    List<String> expectedCommandParams = new ArrayList<>();
    expectedCommandParams.add("test");
    expectedCommandParams.add("1");
    expectedCommandParams.add("2");
    expectedCommandParams.add("3");

    assertEquals(expectedCommandParams, msgTaskRequest.getCommandParameters());
  }
}
