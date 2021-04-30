package com.github.nsilbernagel.discordbot.guard;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExclusiveBotChannelTest {
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private TextChannel exclusiveBotChannel;
  @Mock
  private Message message;

  @Test
  public void it_knows_if_a_message_was_on_the_exclusive_channel() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);

    when(discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.just(exclusiveBotChannel));
    when(message.getChannelId()).thenReturn(exclusiveChannelId);
    when(exclusiveBotChannel.getId()).thenReturn(exclusiveChannelId);

    ExclusiveBotChannel exclusiveBotChannel = new ExclusiveBotChannel(this.discordClient);
    ReflectionTestUtils.setField(exclusiveBotChannel, "exclusiveBotChannelIdString", exclusiveChannelId.asString());
    exclusiveBotChannel.execute();

    assertTrue(exclusiveBotChannel.isOnExclusiveChannel(message));
  }

  @Test
  public void it_knows_if_a_message_was_not_on_the_exclusive_channel() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);
    Snowflake otherChannelId = Snowflake.of(321L);

    when(discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.just(exclusiveBotChannel));
    when(message.getChannelId()).thenReturn(otherChannelId);
    when(exclusiveBotChannel.getId()).thenReturn(exclusiveChannelId);

    ExclusiveBotChannel exclusiveBotChannel = new ExclusiveBotChannel(this.discordClient);
    ReflectionTestUtils.setField(exclusiveBotChannel, "exclusiveBotChannelIdString", exclusiveChannelId.asString());
    exclusiveBotChannel.execute();

    assertFalse(exclusiveBotChannel.isOnExclusiveChannel(message));
  }

  @Test
  public void it_throws_if_the_channel_cannot_be_found() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);

    when(discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.empty());

    ExclusiveBotChannel exclusiveBotChannel = new ExclusiveBotChannel(this.discordClient);
    ReflectionTestUtils.setField(exclusiveBotChannel, "exclusiveBotChannelIdString", exclusiveChannelId.asString());

    assertThrows(RuntimeException.class, exclusiveBotChannel::execute);
  }

  @Test
  public void it_throws_if_the_channel_is_not_a_guild_textchannel() {
    Snowflake exclusiveChannelId = Snowflake.of(123L);
    Channel nonGuildTextChannelChannel = mock(Channel.class);

    when(discordClient.getChannelById(exclusiveChannelId)).thenReturn(Mono.just(nonGuildTextChannelChannel));

    ExclusiveBotChannel exclusiveBotChannel = new ExclusiveBotChannel(this.discordClient);
    ReflectionTestUtils.setField(exclusiveBotChannel, "exclusiveBotChannelIdString", exclusiveChannelId.asString());

    assertThrows(RuntimeException.class, exclusiveBotChannel::execute);
  }
}