package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.publisher.PublisherProbe;

import java.util.Optional;

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

    PublisherProbe<Void> replyMonoProbe = PublisherProbe.empty();
    when(event.replyEphemeral(task.NO_CHANNEL_MESSAGE)).thenReturn(replyMonoProbe.mono());

    this.task.action(request);

    replyMonoProbe.wasSubscribed();
  }

  @Test
  public void it_deletes_the_entry_if_a_channel_was_set_before() {
    TimeVaryingChannelEntity timeVaryingChannel = mock(TimeVaryingChannelEntity.class);
    when(this.timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.of(timeVaryingChannel));

    PublisherProbe<Void> replyMono = PublisherProbe.empty();
    when(event.replyEphemeral(task.UNSET_MESSAGE)).thenReturn(replyMono.mono());

    this.task.action(request);

    verify(timeVaryingChannelRepo).delete(timeVaryingChannel);
    replyMono.wasSubscribed();
  }
}