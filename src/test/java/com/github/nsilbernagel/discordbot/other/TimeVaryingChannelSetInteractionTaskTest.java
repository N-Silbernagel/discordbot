package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.TestableMono;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.channel.Category;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

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
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private VoiceChannel newChannel;

  private TimeVaryingChannelSetInteractionTask task;

  private final Snowflake currentGuildId = Snowflake.of(1L);
  private final Snowflake newChannelId = Snowflake.of(2L);
  private final String newDefaultName = "default";
  private TestableMono<Void> successMsgMono;


  @BeforeEach
  public void setUp() {
    this.task = new TimeVaryingChannelSetInteractionTask(timeVaryingChannelRepo, discordClient);

    when(request.getEvent()).thenReturn(event);
    when(event.getInteraction()).thenReturn(interaction);
    when(interaction.getGuildId()).thenReturn(Optional.of(currentGuildId));

    when(request.getOptionValue("default")).thenReturn(new CommandParam(newDefaultName));
    successMsgMono = new TestableMono<>();
  }

  @Test
  public void it_edits_an_existing_entry() {
    when(discordClient.getChannelById(newChannelId)).thenReturn(Mono.just(newChannel));
    when(request.getOptionValue("channel")).thenReturn(new CommandParam(newChannelId.asString()));
    when(newChannel.getType()).thenReturn(Channel.Type.GUILD_VOICE);
    Snowflake oldChannelId = Snowflake.of(3L);
    TimeVaryingChannelEntity existingTimeVaryingChannel = spy(new TimeVaryingChannelEntity(oldChannelId.asLong(), currentGuildId.asLong(), newDefaultName));

    when(event.replyEphemeral(task.PERSIST_MESSAGE)).thenReturn(successMsgMono.getMono());

    when(timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.of(existingTimeVaryingChannel));

    when(request.getOptionValue("morning")).thenReturn(CommandParam.empty());
    when(request.getOptionValue("noon")).thenReturn(CommandParam.empty());
    when(request.getOptionValue("evening")).thenReturn(CommandParam.empty());

    task.action(request);

    verify(existingTimeVaryingChannel).setChannelId(newChannelId.asLong());
    verify(timeVaryingChannelRepo).save(existingTimeVaryingChannel);
  }

  @Test
  public void it_persists_the_channels_names() {
    when(discordClient.getChannelById(newChannelId)).thenReturn(Mono.just(newChannel));
    when(request.getOptionValue("channel")).thenReturn(new CommandParam(newChannelId.asString()));
    when(newChannel.getType()).thenReturn(Channel.Type.GUILD_VOICE);
    when(event.replyEphemeral(TimeVaryingChannelSetInteractionTask.PERSIST_MESSAGE)).thenReturn(successMsgMono.getMono());

    TimeVaryingChannelEntity expectedChannelEntity = new TimeVaryingChannelEntity(newChannelId.asLong(), currentGuildId.asLong(), newDefaultName);
    String mName = "test1";
    String nName = "test2";
    String eName = "test3";
    expectedChannelEntity.setMorningName(mName);
    expectedChannelEntity.setNoonName(nName);
    expectedChannelEntity.setEveningName(eName);

    when(request.getOptionValue("morning")).thenReturn(new CommandParam(mName));
    when(request.getOptionValue("noon")).thenReturn(new CommandParam(nName));
    when(request.getOptionValue("evening")).thenReturn(new CommandParam(eName));

    when(timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.empty());

    task.action(request);

    verify(timeVaryingChannelRepo).save(eq(expectedChannelEntity));
  }

  @ParameterizedTest
  @MethodSource("channelTypesProvider")
  public void it_only_persists_guild_voice_channels(Class<Channel> klass, Channel.Type channelType, boolean shouldGetSaved) {
    Snowflake givenChannelId = Snowflake.of(6L);
    Channel givenChannel = mock(klass);
    when(givenChannel.getType()).thenReturn(channelType);

    when(discordClient.getChannelById(givenChannelId)).thenReturn(Mono.just(givenChannel));
    when(request.getOptionValue("channel")).thenReturn(new CommandParam(givenChannelId.asString()));

    TestableMono<Void> wrongTypeMono = new TestableMono<>();
    if(shouldGetSaved) {
      when(timeVaryingChannelRepo.findByguildId(currentGuildId.asLong())).thenReturn(Optional.empty());
      when(event.replyEphemeral(TimeVaryingChannelSetInteractionTask.PERSIST_MESSAGE)).thenReturn(successMsgMono.getMono());
    } else {
      when(event.replyEphemeral(TimeVaryingChannelSetInteractionTask.WRONG_TYPE_MESSAGE)).thenReturn(wrongTypeMono.getMono());
    }

    when(request.getOptionValue("morning")).thenReturn(CommandParam.empty());
    when(request.getOptionValue("noon")).thenReturn(CommandParam.empty());
    when(request.getOptionValue("evening")).thenReturn(CommandParam.empty());

    task.action(request);

    verify(timeVaryingChannelRepo, times(shouldGetSaved ? 1 : 0)).save(any(TimeVaryingChannelEntity.class));
    if(shouldGetSaved) {
      assertTrue(successMsgMono.wasSubscribedTo());
      assertFalse(wrongTypeMono.wasSubscribedTo());
    } else {
      assertTrue(wrongTypeMono.wasSubscribedTo());
      assertFalse(successMsgMono.wasSubscribedTo());
    }
  }

  private static Object[][] channelTypesProvider() {
    return new Object[][] {
        {TextChannel.class, Channel.Type.GUILD_TEXT, false},
        {VoiceChannel.class, Channel.Type.GUILD_VOICE, true},
        {TextChannel.class, Channel.Type.DM, false},
        {Category.class, Channel.Type.GUILD_CATEGORY, false}
    };
  }
}