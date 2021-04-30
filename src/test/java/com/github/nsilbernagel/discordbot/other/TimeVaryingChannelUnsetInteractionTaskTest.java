package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.TestableMono;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeVaryingChannelUnsetInteractionTaskTest {
  @Mock
  private TimeVaryingChannelRepo timeVaryingChannelRepo;
  @Mock
  private InteractionTaskRequest request;
  @Mock
  private InteractionCreateEvent event;
  @Mock
  private Interaction interaction;

  private TimeVaryingChannelUnsetInteractionTask task;
  private final Snowflake currentGuildId = Snowflake.of(1L);

  @BeforeEach
  public void setUp() {
    this.task = new TimeVaryingChannelUnsetInteractionTask(timeVaryingChannelRepo);

    when(request.getEvent()).thenReturn(event);
    when(event.getInteraction()).thenReturn(interaction);
    when(interaction.getGuildId()).thenReturn(Optional.of(currentGuildId));
  }

  @Test
  public void it_replies_ephermally_if_no_time_varying_channel_was_set_before() {
    when(this.timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.empty());

    TestableMono<Void> replyMono = new TestableMono<>();
    when(event.replyEphemeral(task.NO_CHANNEL_MESSAGE)).thenReturn(replyMono.getMono());

    this.task.action(request);

    assertTrue(replyMono.wasSubscribedTo());
  }

  @Test
  public void it_deletes_the_entry_if_a_channel_was_set_before() {
    TimeVaryingChannelEntity timeVaryingChannel = mock(TimeVaryingChannelEntity.class);
    when(this.timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.of(timeVaryingChannel));

    TestableMono<Void> replyMono = new TestableMono<>();
    when(event.replyEphemeral(task.UNSET_MESSAGE)).thenReturn(replyMono.getMono());

    this.task.action(request);

    verify(timeVaryingChannelRepo).delete(timeVaryingChannel);
    assertTrue(replyMono.wasSubscribedTo());
  }
}