package com.github.nsilbernagel.discordbot.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

@Component
public class LavaTrackScheduler extends AudioEventAdapter {

  private AudioTrack currentTrack;

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  @Autowired
  private GatewayDiscordClient discordClient;

  private final BlockingQueue<AudioTrack> queue;

  public LavaTrackScheduler() {
    this.queue = new LinkedBlockingDeque<AudioTrack>();
  }

  /**
   * @param track to queue
   * @return queue success
   */
  public boolean queue(AudioTrack track) {
    boolean trackImmediatelyStarted = this.lavaPlayerAudioProvider.getPlayer()
        .startTrack(track, true);

    if (trackImmediatelyStarted) {
      return true;
    }

    return this.queue.offer(track);
  }

  /**
   * Start the next track, stopping the current one if it is playing.
   */
  public void nextTrack() {
    AudioTrack nextTrack = queue.poll();

    if (nextTrack == null) {
      this.setPresenceOnline();
    }

    this.lavaPlayerAudioProvider.getPlayer()
        .startTrack(nextTrack, false);
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or
    // LOAD_FAILED)
    if (endReason.mayStartNext) {
      nextTrack();
    } else {
      this.setPresenceOnline();
    }
  }

  @Override
  public void onPlayerPause(AudioPlayer player) {
    if (this.currentTrack == null) {
      return;
    }
    this.discordClient.updatePresence(Presence.online(Activity.playing("⏸" + this.currentTrack.getInfo().title)))
        .subscribe();
  }

  @Override
  public void onPlayerResume(AudioPlayer player) {
    if (this.currentTrack == null) {
      return;
    }
    this.discordClient.updatePresence(Presence.online(Activity.playing(this.currentTrack.getInfo().title)))
        .subscribe();
  }

  @Override
  public void onTrackStart(AudioPlayer player, AudioTrack track) {
    this.setPresencePlayingTrack(track);
  }

  private void setPresencePlayingTrack(AudioTrack track) {
    this.currentTrack = track;

    this.discordClient
        .updatePresence(Presence.online(Activity.playing(track.getInfo().title)))
        .subscribe();
  }

  private void setPresenceOnline() {
    this.currentTrack = null;

    this.discordClient
        .updatePresence(Presence.online())
        .subscribe();
  }
}
