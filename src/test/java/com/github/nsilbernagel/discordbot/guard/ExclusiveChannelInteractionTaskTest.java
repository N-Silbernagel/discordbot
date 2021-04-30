package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.TestableMono;
import com.github.nsilbernagel.discordbot.interaction.InteractionTaskRequest;
import com.github.nsilbernagel.discordbot.message.validation.CommandParam;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExclusiveChannelInteractionTaskTest {
  @Mock
  private InteractionTaskRequest request;
  @Mock
  private ExclusiveChannelRepository exclusiveChannelRepository;
  @Mock
  private InteractionCreateEvent interactionCreateEvent;
  @Mock
  private ExclusiveChannelEntity existingExclusiveChannel;
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private Interaction interaction;

  private final Snowflake guildId = Snowflake.of(123L);

  private ExclusiveChannelInteractionTask exclusiveChannelInteractionTask;

  @BeforeEach
  public void setUp() {
    this.exclusiveChannelInteractionTask = new ExclusiveChannelInteractionTask(this.exclusiveChannelRepository, this.discordClient);
    when(this.request.getEvent()).thenReturn(this.interactionCreateEvent);
    when(this.interactionCreateEvent.getInteraction()).thenReturn(this.interaction);
    when(this.interaction.getGuildId()).thenReturn(Optional.of(this.guildId));
  }

  @Test
  public void it_replies_ephermaly_when_no_channel_was_given_and_non_was_set_before() {
    when(this.request.getOptionValue("channel")).thenReturn(CommandParam.empty());
    when(this.exclusiveChannelRepository.findByguildId(any(long.class))).thenReturn(Optional.empty());

    TestableMono<Void> replyMono = new TestableMono<>();
    when(this.interactionCreateEvent.replyEphemeral(anyString())).thenReturn(replyMono.getMono());

    exclusiveChannelInteractionTask.action(this.request);

    assertTrue(replyMono.wasSubscribedTo());
  }

  @Test
  public void it_deletes_a_guilds_exclusive_channel_when_no_channel_was_given() {
    when(this.request.getOptionValue("channel")).thenReturn(CommandParam.empty());
    when(this.exclusiveChannelRepository.findByguildId(any(long.class))).thenReturn(Optional.of(this.existingExclusiveChannel));

    TestableMono<Void> replyMono = new TestableMono<>();
    when(this.request.getEvent().replyEphemeral(anyString())).thenReturn(replyMono.getMono());

    exclusiveChannelInteractionTask.action(this.request);

    assertTrue(replyMono.wasSubscribedTo());
    verify(this.exclusiveChannelRepository).delete(eq(this.existingExclusiveChannel));
  }

  @Test
  public void it_replies_ephermaly_when_a_voice_channel_was_given() {
    Snowflake voiceChannelId = Snowflake.of(123L);
    VoiceChannel givenVoiceChannel = mock(VoiceChannel.class);
    when(givenVoiceChannel.getType()).thenReturn(Channel.Type.GUILD_VOICE);

    when(this.request.getOptionValue("channel")).thenReturn(new CommandParam(voiceChannelId.asString()));

    TestableMono<Void> replyMono = new TestableMono<>();
    when(this.interactionCreateEvent.replyEphemeral(anyString())).thenReturn(replyMono.getMono());

    TestableMono<Channel> getChannelMono = new TestableMono<>(Mono.just(givenVoiceChannel));
    when(this.discordClient.getChannelById(voiceChannelId)).thenReturn(getChannelMono.getMono());

    exclusiveChannelInteractionTask.action(this.request);

    assertTrue(replyMono.wasSubscribedTo());
  }

  @Test
  public void it_sets_a_given_text_channel_as_the_current_guilds_exclusive_channel() {
    Snowflake textChannelId = Snowflake.of(123L);
    TextChannel givenTextChannel = mock(TextChannel.class);
    when(givenTextChannel.getType()).thenReturn(Channel.Type.GUILD_TEXT);

    when(this.request.getOptionValue("channel")).thenReturn(new CommandParam(textChannelId.asString()));

    TestableMono<Void> replyMono = new TestableMono<>();
    when(this.request.getEvent().replyEphemeral(anyString())).thenReturn(replyMono.getMono());

    TestableMono<Channel> getChannelMono = new TestableMono<>(Mono.just(givenTextChannel));
    when(this.discordClient.getChannelById(textChannelId)).thenReturn(getChannelMono.getMono());

    exclusiveChannelInteractionTask.action(this.request);

    assertTrue(replyMono.wasSubscribedTo());
    verify(this.exclusiveChannelRepository).save(eq(new ExclusiveChannelEntity(textChannelId.asLong(), this.guildId.asLong())));
  }

  @Test
  public void it_overwrites_the_exclusive_channel_if_the_guild_already_has_an_exclusive_channel() {
    Snowflake textChannelId = Snowflake.of(321L);
    TextChannel givenTextChannel = mock(TextChannel.class);
    when(givenTextChannel.getType()).thenReturn(Channel.Type.GUILD_TEXT);

    when(this.request.getOptionValue("channel")).thenReturn(new CommandParam(textChannelId.asString()));

    when(this.exclusiveChannelRepository.findByguildId(any(long.class))).thenReturn(Optional.of(this.existingExclusiveChannel));

    TestableMono<Void> replyMono = new TestableMono<>();
    when(this.request.getEvent().replyEphemeral(anyString())).thenReturn(replyMono.getMono());

    TestableMono<Channel> getChannelMono = new TestableMono<>(Mono.just(givenTextChannel));
    when(this.discordClient.getChannelById(textChannelId)).thenReturn(getChannelMono.getMono());

    exclusiveChannelInteractionTask.action(this.request);

    assertTrue(replyMono.wasSubscribedTo());
    verify(this.existingExclusiveChannel).setChannelId(textChannelId.asLong());
    verify(this.exclusiveChannelRepository).save(eq(this.existingExclusiveChannel));
  }
}