package com.github.nsilbernagel.discordbot.guard;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExclusiveBotChannelTest {
  @Mock
  private ExclusiveChannelRepository exclusiveChannelRepository;
  @Mock
  private ExclusiveChannelEntity exclusiveChannelEntity;
  @Mock
  private TextChannel exclusiveChannel;
  @Mock
  private Message message;
  @Mock
  private GatewayDiscordClient discordClient;

  private ExclusiveBotChannel exclusiveBotChannel;

  private final Snowflake guildId = Snowflake.of(1L);

  @BeforeEach
  public void setUp() {
    this.exclusiveBotChannel = new ExclusiveBotChannel(this.exclusiveChannelRepository, this.discordClient);
  }

  @Test
  public void it_knows_if_a_message_was_on_the_exclusive_channel() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);

    when(this.discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.just(this.exclusiveChannel));
    when(message.getGuildId()).thenReturn(Optional.of(guildId));
    when(exclusiveChannelRepository.findByguildId(guildId.asLong())).thenReturn(Optional.of(exclusiveChannelEntity));
    when(message.getChannelId()).thenReturn(exclusiveChannelId);

    when(exclusiveChannelEntity.getChannelId()).thenReturn(exclusiveChannelId.asLong());

    assertTrue(exclusiveBotChannel.isOnExclusiveChannel(message));
  }

  @Test
  public void it_knows_if_a_message_was_not_on_the_exclusive_channel() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);
    Snowflake otherChannelId = Snowflake.of(321L);

    when(this.discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.just(this.exclusiveChannel));
    when(message.getGuildId()).thenReturn(Optional.of(guildId));
    when(exclusiveChannelRepository.findByguildId(guildId.asLong())).thenReturn(Optional.of(exclusiveChannelEntity));
    when(message.getChannelId()).thenReturn(otherChannelId);

    when(exclusiveChannelEntity.getChannelId()).thenReturn(exclusiveChannelId.asLong());

    assertFalse(exclusiveBotChannel.isOnExclusiveChannel(message));
  }

  @Test
  public void it_throws_if_the_channel_cannot_be_found() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);

    when(this.discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.empty());
    when(message.getGuildId()).thenReturn(Optional.of(guildId));
    when(exclusiveChannelRepository.findByguildId(guildId.asLong())).thenReturn(Optional.of(exclusiveChannelEntity));

    when(exclusiveChannelEntity.getChannelId()).thenReturn(exclusiveChannelId.asLong());

    assertThrows(ChannelNotFoundException.class, () -> exclusiveBotChannel.isOnExclusiveChannel(message));
  }

  @Test
  public void it_deletes_a_message_on_another_channel() {
    PublisherProbe<Void> deleteMono = PublisherProbe.empty();
    when(message.delete()).thenReturn(deleteMono.mono());
    when(message.getAuthor()).thenReturn(Optional.empty());

    exclusiveBotChannel.handleMessageOnOtherChannel(message);

    deleteMono.assertWasSubscribed();
  }
}