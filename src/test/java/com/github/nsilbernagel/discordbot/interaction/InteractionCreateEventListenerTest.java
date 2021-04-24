package com.github.nsilbernagel.discordbot.interaction;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.discordjson.json.ApplicationCommandInteractionData;
import discord4j.discordjson.json.InteractionData;
import discord4j.discordjson.possible.Possible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionCreateEventListenerTest {
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private Environment env;
  @Mock
  private InteractionTask interactionTaskOne;
  @Mock
  private InteractionTask interactionTaskTwo;
  @Mock
  private InteractionCreateEvent interactionCreateEventMock;
  @Mock
  private Interaction interactionMock;
  @Mock
  private InteractionData interactionData;
  @Mock
  private ApplicationCommandInteractionData applicationCommandInteractionData;

  private final List<InteractionTask> interactionTasks = new ArrayList<>();
  private InteractionCreateEventListener interactionCreateEventListener;

  private final String testCommand = "test";

  @BeforeEach
  public void setUp() {
    when(this.interactionCreateEventMock.getCommandName()).thenReturn(this.testCommand);

    when(this.interactionCreateEventMock.getInteraction()).thenReturn(this.interactionMock);
    when(this.interactionMock.getData()).thenReturn(this.interactionData);
    when(this.interactionData.data()).thenReturn(Possible.of(this.applicationCommandInteractionData));

    when(this.applicationCommandInteractionData.options()).thenReturn(Possible.absent());

    this.interactionCreateEventListener = new InteractionCreateEventListener(this.discordClient, this.env, this.interactionTasks);
    this.interactionTasks.add(this.interactionTaskOne);
    this.interactionTasks.add(this.interactionTaskTwo);
  }

  @Test
  public void it_executes_the_fitting_tasks() {
    when(this.interactionTaskOne.canHandle(eq(this.testCommand))).thenReturn(true);
    when(this.interactionTaskTwo.canHandle(eq(this.testCommand))).thenReturn(true);

    this.interactionCreateEventListener.execute(this.interactionCreateEventMock);

    verify(this.interactionTaskOne).execute(any(InteractionTaskRequest.class));
    verify(this.interactionTaskTwo).execute(any(InteractionTaskRequest.class));
  }

  @Test
  public void it_does_not_execute_the_tasks_that_dont_fit() {
    when(this.interactionTaskOne.canHandle(eq(this.testCommand))).thenReturn(true);
    when(this.interactionTaskTwo.canHandle(eq(this.testCommand))).thenReturn(false);

    this.interactionCreateEventListener.execute(this.interactionCreateEventMock);

    verify(this.interactionTaskOne).execute(any(InteractionTaskRequest.class));
    verify(this.interactionTaskTwo, times(0)).execute(any(InteractionTaskRequest.class));
  }
}