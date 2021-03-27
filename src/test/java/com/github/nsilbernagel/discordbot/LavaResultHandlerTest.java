package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.audio.LavaResultHandler;
import com.github.nsilbernagel.discordbot.audio.LavaTrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class LavaResultHandlerTest {
  @Mock
  private LavaTrackScheduler lavaTrackSchedulerMock;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void there_is_a_threshold_for_how_many_items_of_a_playlist_are_queued() {
    LavaResultHandler lavaResultHandler = new LavaResultHandler(this.lavaTrackSchedulerMock);

    AudioPlaylist audioPlaylistMock = Mockito.mock(AudioPlaylist.class);

    List<AudioTrack> audioTrackMocks = new ArrayList<>(LavaResultHandler.playlistThreshold + 1);

    IntStream.range(0, LavaResultHandler.playlistThreshold)
        .forEach((i) -> audioTrackMocks.add(Mockito.mock(AudioTrack.class)));

    Mockito.when(audioPlaylistMock.getTracks()).thenReturn(audioTrackMocks);

    lavaResultHandler.playlistLoaded(audioPlaylistMock);

    Mockito.verify(lavaTrackSchedulerMock, Mockito.times(LavaResultHandler.playlistThreshold)).queue(Mockito.any(AudioTrack.class));
  }
}
