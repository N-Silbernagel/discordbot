package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.TestableMono;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
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
class TimeVaryingChannelSetInteractionTaskTest {
  @Mock
  private TimeVaryingChannelRepo timeVaryingChannelRepo;
  @Mock
  private InteractionTaskRequest request;
  @Mock
  private InteractionCreateEvent event;
  @Mock
  private Interaction interaction;

  private TimeVaryingChannelSetInteractionTask task;

  private final Snowflake currentGuildId = Snowflake.of(1L);
  private final Snowflake newChannelId = Snowflake.of(2L);
  private final String newDefaultName = "default";
  private TestableMono<Void> replyMono;


  @BeforeEach
  public void setUp() {
    this.task = new TimeVaryingChannelSetInteractionTask(timeVaryingChannelRepo);

    when(request.getEvent()).thenReturn(event);
    when(event.getInteraction()).thenReturn(interaction);
    when(interaction.getGuildId()).thenReturn(Optional.of(currentGuildId));

    when(request.getOptionValue("channel")).thenReturn(new CommandParam(newChannelId.asString()));
    when(request.getOptionValue("default")).thenReturn(new CommandParam(newDefaultName));

    replyMono = new TestableMono<>();
    when(event.replyEphemeral(task.PERSIST_MESSAGE)).thenReturn(replyMono.getMono());
  }

  @Test
  public void it_persists_the_given_channel() {
    TimeVaryingChannelEntity expectedTimeVaryingChannel = new TimeVaryingChannelEntity(newChannelId.asLong(), currentGuildId.asLong(), newDefaultName);

    when(timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.empty());

    task.action(request);

    verify(timeVaryingChannelRepo).save(eq(expectedTimeVaryingChannel));
  }

  @Test
  public void it_responds_after_persisting() {
    when(timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.empty());

    task.action(request);

    assertTrue(replyMono.wasSubscribedTo());
  }

  @Test
  public void it_edits_an_existing_entry() {
    Snowflake oldChannelId = Snowflake.of(3L);
    TimeVaryingChannelEntity existingTimeVaryingChannel = spy(new TimeVaryingChannelEntity(oldChannelId.asLong(), currentGuildId.asLong(), newDefaultName));

    when(timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.of(existingTimeVaryingChannel));

    task.action(request);

    verify(existingTimeVaryingChannel).setChannelId(newChannelId.asLong());
    verify(timeVaryingChannelRepo).save(existingTimeVaryingChannel);
  }
}