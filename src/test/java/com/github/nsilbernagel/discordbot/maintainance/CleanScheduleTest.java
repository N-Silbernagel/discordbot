package com.github.nsilbernagel.discordbot.maintainance;

import com.github.nsilbernagel.discordbot.guard.ExclusiveChannelEntity;
import com.github.nsilbernagel.discordbot.guard.ExclusiveChannelRepository;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanScheduleTest {
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private ChannelCleaner channelCleaner;
  @Mock
  private ExclusiveChannelRepository exclusiveChannelRepo;

  @Test
  public void it_runs_the_channel_cleaner_on_all_exclusive_channels() {
    CleanSchedule cleanSchedule = spy(new CleanSchedule(discordClient, channelCleaner, exclusiveChannelRepo));

    int numberOfCleanChannels = 3;

    Map<ExclusiveChannelEntity, TextChannel> exclusiveChannels = this.cleanChannels(numberOfCleanChannels);

    when(exclusiveChannelRepo.findAll()).thenReturn(exclusiveChannels.keySet());

    cleanSchedule.cleanBotChannels();

    for (TextChannel channel: exclusiveChannels.values()) {
      verify(channelCleaner).execute(eq(channel));
    }
  }

  private Map<ExclusiveChannelEntity, TextChannel> cleanChannels(int numberOfChannels) {
    Map<ExclusiveChannelEntity, TextChannel> cleanChannels = new HashMap<>(numberOfChannels);
    for (int i = 0; i < numberOfChannels; i++) {
      Snowflake cleanChannelId = Snowflake.of(i);
      TextChannel cleanChannel = mock(TextChannel.class);
      ExclusiveChannelEntity channelEntity = mock(ExclusiveChannelEntity.class);

      when(channelEntity.getChannelId()).thenReturn(cleanChannelId.asLong());
      when(discordClient.getChannelById(cleanChannelId)).thenReturn(Mono.just(cleanChannel));
      cleanChannels.put(channelEntity, cleanChannel);
    }
    return cleanChannels;
  }
}