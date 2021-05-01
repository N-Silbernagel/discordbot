package com.github.nsilbernagel.discordbot.other;

import com.github.nsilbernagel.discordbot.TestableMono;
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
import org.springframework.test.util.ReflectionTestUtils;

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

  @BeforeEach
  public void setUp() {
    channelNameClock = spy(new ChannelNameClock(discordClient));
  }

  @Test
  public void it_sets_the_channels_name_to_the_default_name_on_shutdown() {
    ReflectionTestUtils.setField(channelNameClock, "channel", renameChannel);

    ArgumentCaptor<Consumer<VoiceChannelEditSpec>> renameCaptor = ArgumentCaptor.forClass(Consumer.class);
    TestableMono<VoiceChannel> editMono = new TestableMono<>();
    when(renameChannel.edit(renameCaptor.capture())).thenReturn(editMono.getMono());

    channelNameClock.shutdown();

    renameCaptor.getValue().accept(editSpec);

    verify(editSpec).setName(ChannelNameClock.DEFAULT_NAME);
  }

  @ParameterizedTest
  @MethodSource("nameAtTimeOfDay")
  public void name_includes_the_appropriate_name_and_the_hour_at_a_certain_time_of_day(int timeOfDay, String nameAtTimeOfDay) {
    when(channelNameClock.getHourOfDay()).thenReturn(timeOfDay);

    ReflectionTestUtils.setField(channelNameClock, "channel", renameChannel);

    ArgumentCaptor<Consumer<VoiceChannelEditSpec>> renameCaptor = ArgumentCaptor.forClass(Consumer.class);
    TestableMono<VoiceChannel> editMono = new TestableMono<>();
    when(renameChannel.edit(renameCaptor.capture())).thenReturn(editMono.getMono());

    channelNameClock.changeChannelName();

    renameCaptor.getValue().accept(editSpec);

    ArgumentCaptor<String> newName = ArgumentCaptor.forClass(String.class);
    verify(editSpec).setName(newName.capture());
    assertTrue(newName.getValue().contains(nameAtTimeOfDay));
    assertTrue(newName.getValue().contains(String.valueOf(timeOfDay)));
  }

  public static Object[][] nameAtTimeOfDay() {
    return new Object[][] {
      {ChannelNameClock.MORNING_BEGIN, ChannelNameClock.MORNING_NAME},
      {ChannelNameClock.NOON_BEGIN, ChannelNameClock.NOON_NAME},
      {ChannelNameClock.EVENING_BEGIN, ChannelNameClock.EVENING_NAME}
    };
  }
}