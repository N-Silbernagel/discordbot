package com.github.nsilbernagel.discordbot.interaction;

import com.github.nsilbernagel.discordbot.TestableMono;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.discordjson.json.ApplicationCommandInteractionData;
import discord4j.discordjson.json.InteractionData;
import discord4j.discordjson.possible.Possible;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
  private List<InteractionTask> interactionTasks = new ArrayList<>();
  private InteractionCreateEventListener interactionCreateEventListener;
  private TestableMono<Void> replyMono;

  private String testCommand = "test";

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    this.replyMono = new TestableMono<>();

    when(this.interactionCreateEventMock.getInteraction()).thenReturn(this.interactionMock);
    when(this.interactionMock.getData()).thenReturn(this.interactionData);
    when(this.interactionData.data()).thenReturn(Possible.of(this.applicationCommandInteractionData));

    when(this.interactionCreateEventMock.getCommandName()).thenReturn(this.testCommand);

    when(this.interactionCreateEventMock.reply(anyString())).thenReturn(this.replyMono.getMono());

    this.interactionCreateEventListener = new InteractionCreateEventListener(this.discordClient, this.env, this.interactionTasks);
    this.interactionTasks.add(this.interactionTaskOne);
    this.interactionTasks.add(this.interactionTaskTwo);
  }

  @Test
  public void it_executes_the_fitting_tasks() {
    when(this.interactionTaskOne.canHandle(eq(this.testCommand))).thenReturn(true);
    when(this.interactionTaskTwo.canHandle(eq(this.testCommand))).thenReturn(true);

    this.interactionCreateEventListener.execute(this.interactionCreateEventMock);

    verify(this.interactionTaskOne).execute(eq(this.interactionCreateEventMock));
    verify(this.interactionTaskTwo).execute(eq(this.interactionCreateEventMock));
  }

  @Test
  public void it_executes_the_tasks_not_fitting() {
    when(this.interactionTaskOne.canHandle(eq(this.testCommand))).thenReturn(true);
    when(this.interactionTaskTwo.canHandle(eq(this.testCommand))).thenReturn(false);

    this.interactionCreateEventListener.execute(this.interactionCreateEventMock);

    verify(this.interactionTaskOne).execute(eq(this.interactionCreateEventMock));
    verify(this.interactionTaskTwo, times(0)).execute(eq(this.interactionCreateEventMock));
  }
}