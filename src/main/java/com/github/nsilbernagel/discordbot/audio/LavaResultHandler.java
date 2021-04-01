package com.github.nsilbernagel.discordbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.Getter;

public final class LavaResultHandler implements AudioLoadResultHandler {
  /**
   * Threshold for how many items of a playlist may be queued
   */
  public static final int playlistThreshold = 5;

  private final LavaTrackScheduler lavaTrackScheduler;

  @Getter
  private final String id;

  public LavaResultHandler(LavaTrackScheduler lavaTrackScheduler, String id) {
    this.id = id;
    this.lavaTrackScheduler = lavaTrackScheduler;
  }

  @Override
  public void trackLoaded(final AudioTrack track) {
    // LavaPlayer found an audio source for us to play
    this.lavaTrackScheduler.queue(track, this.id);
  }

  @Override
  public void playlistLoaded(final AudioPlaylist playlist) {
    playlist.getTracks()
        .stream()
        .limit(playlistThreshold)
        .forEach(audioTrack -> this.lavaTrackScheduler.queue(audioTrack, this.id));
  }

  @Override
  public void noMatches() {
    // LavaPlayer did not find any audio to extract
  }

  @Override
  public void loadFailed(FriendlyException exception) {
    // LavaPlayer could not parse an audio source for some reason
  }
}
