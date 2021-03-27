package com.github.nsilbernagel.discordbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import org.springframework.stereotype.Component;

@Component
public final class LavaResultHandler implements AudioLoadResultHandler {
  /**
   * Threshold for how many items of a playlist may be queued
   */
  public static final int playlistThreshold = 5;

  private final LavaTrackScheduler lavaTrackScheduler;

  public LavaResultHandler(LavaTrackScheduler lavaTrackScheduler) {
    this.lavaTrackScheduler = lavaTrackScheduler;
  }

  @Override
  public void trackLoaded(final AudioTrack track) {
    // LavaPlayer found an audio source for us to play
    this.lavaTrackScheduler.queue(track);
  }

  @Override
  public void playlistLoaded(final AudioPlaylist playlist) {
    playlist.getTracks()
        .stream()
        .limit(playlistThreshold)
        .forEach(this.lavaTrackScheduler::queue);
  }

  @Override
  public void noMatches() throws LavaPlayerException {
    // LavaPlayer did not find any audio to extract
    throw new LavaPlayerException("Audio konnte nicht gefunden werden.");
  }

  @Override
  public void loadFailed(final FriendlyException e) throws LavaPlayerException {
    // LavaPlayer could not parse an audio source for some reason
    throw new LavaPlayerException("Audio konnte nicht geladen werden.");
  }
}
