package com.github.nsilbernagel.discordbot.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LavaTrackScheduler extends AudioEventAdapter {

  @Autowired
  private LavaPlayerAudioProvider lavaPlayerAudioProvider;

  private final BlockingQueue<AudioTrack> queue;

  public LavaTrackScheduler() {
    this.queue = new LinkedBlockingDeque<AudioTrack>();
  }

  /**
   *
   * @param track
   *                to queue
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
    this.lavaPlayerAudioProvider.getPlayer()
        .startTrack(queue.poll(), false);
  }

  @Override
  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
    // Only start the next track if the end reason is suitable for it (FINISHED or
    // LOAD_FAILED)
    if (endReason.mayStartNext) {
      nextTrack();
    }
  }
}
