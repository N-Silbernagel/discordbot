package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.TaskException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public final class TrackScheduler implements AudioLoadResultHandler {

  private final AudioPlayer player;

  public TrackScheduler(final AudioPlayer player) {
    this.player = player;
  }

  @Override
  public void trackLoaded(final AudioTrack track) {
    // LavaPlayer found an audio source for us to play
    player.playTrack(track);
  }

  @Override
  public void playlistLoaded(final AudioPlaylist playlist) {
    // LavaPlayer found multiple AudioTracks from some playlist
  }

  @Override
  public void noMatches() {
    // LavaPlayer did not find any audio to extract
    throw new TaskException("Kein Video zu dem Link gefunden");
  }

  @Override
  public void loadFailed(final FriendlyException e) {
    // LavaPlayer could not parse an audio source for some reason
    throw new TaskException("Konnte das Video nicht laden", e);
  }
}
