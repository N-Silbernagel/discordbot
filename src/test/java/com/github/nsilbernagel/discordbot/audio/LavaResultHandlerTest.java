package com.github.nsilbernagel.discordbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
public class LavaResultHandlerTest {
  @Mock
  private LavaTrackScheduler lavaTrackSchedulerMock;

  @Test
  public void there_is_a_threshold_for_how_many_items_of_a_playlist_are_queued() {
    String audioSourceIdFake = "test";

    LavaResultHandler lavaResultHandler = new LavaResultHandler(this.lavaTrackSchedulerMock, audioSourceIdFake);

    AudioPlaylist audioPlaylistMock = Mockito.mock(AudioPlaylist.class);

    List<AudioTrack> audioTrackMocks = new ArrayList<>(LavaResultHandler.playlistThreshold + 1);

    IntStream.range(0, LavaResultHandler.playlistThreshold)
        .forEach((i) -> audioTrackMocks.add(Mockito.mock(AudioTrack.class)));

    Mockito.when(audioPlaylistMock.getTracks()).thenReturn(audioTrackMocks);

    lavaResultHandler.playlistLoaded(audioPlaylistMock);

    Mockito.verify(lavaTrackSchedulerMock, Mockito.times(LavaResultHandler.playlistThreshold)).queue(Mockito.any(AudioTrack.class), Mockito.eq(audioSourceIdFake));
  }
}
