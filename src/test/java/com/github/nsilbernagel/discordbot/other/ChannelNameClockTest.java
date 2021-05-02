package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.TestableMono;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.VoiceChannelEditSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelNameClockTest {
  @Mock
  private ChannelNameClock channelNameClock;
  @Mock
  private GatewayDiscordClient discordClient;
  @Mock
  private VoiceChannel renameChannel;
  @Mock
  private VoiceChannelEditSpec editSpec;
  @Mock
  private TimeVaryingChannelRepo repo;
  @Mock
  private TimeVaryingChannelEntity renameChannelEntity;

  private final Snowflake renameChannelId = Snowflake.of(1L);

  private final static String morningName = "test1";
  private final static String noonName = "test1";
  private final static String eveName = "test1";

  @BeforeEach
  public void setUp() {
    channelNameClock = spy(new ChannelNameClock(discordClient, repo));

    Iterable<TimeVaryingChannelEntity> timeVaryingChannelEntities = List.of(renameChannelEntity);
    when(renameChannelEntity.getChannelId()).thenReturn(renameChannelId.asLong());
    when(discordClient.getChannelById(renameChannelId)).thenReturn(Mono.just(renameChannel));

    when(repo.findAll()).thenReturn(timeVaryingChannelEntities);
  }

  @Test
  public void it_sets_the_channel_names_to_their_default_names_on_shutdown() {
    ArgumentCaptor<Consumer<VoiceChannelEditSpec>> renameCaptor = ArgumentCaptor.forClass(Consumer.class);
    TestableMono<VoiceChannel> editMono = new TestableMono<>();
    when(renameChannel.edit(renameCaptor.capture())).thenReturn(editMono.getMono());

    channelNameClock.shutdown();

    renameCaptor.getValue().accept(editSpec);

    verify(editSpec).setName(renameChannelEntity.getDefaultName());
  }

  @ParameterizedTest
  @MethodSource("nameAtTimeOfDay")
  public void name_includes_the_appropriate_name_and_the_hour_at_a_certain_time_of_day(int timeOfDay, String nameAtTimeOfDay) {
    when(channelNameClock.getHourOfDay()).thenReturn(timeOfDay);

    ArgumentCaptor<Consumer<VoiceChannelEditSpec>> renameCaptor = ArgumentCaptor.forClass(Consumer.class);
    TestableMono<VoiceChannel> editMono = new TestableMono<>();
    when(renameChannel.edit(renameCaptor.capture())).thenReturn(editMono.getMono());

    lenient().when(renameChannelEntity.getMorningName()).thenReturn(Optional.of(morningName));
    lenient().when(renameChannelEntity.getNoonName()).thenReturn(Optional.of(noonName));
    lenient().when(renameChannelEntity.getEveningName()).thenReturn(Optional.of(eveName));

    channelNameClock.changeChannelNames();

    renameCaptor.getValue().accept(editSpec);

    ArgumentCaptor<String> newName = ArgumentCaptor.forClass(String.class);
    verify(editSpec).setName(newName.capture());

    assertTrue(newName.getValue().contains(nameAtTimeOfDay));
    assertTrue(newName.getValue().contains(String.valueOf(timeOfDay)));
  }

  public static Object[][] nameAtTimeOfDay() {
    return new Object[][] {
      {ChannelNameClock.MORNING_BEGIN, morningName},
      {ChannelNameClock.NOON_BEGIN, noonName},
      {ChannelNameClock.EVENING_BEGIN, eveName}
    };
  }
}