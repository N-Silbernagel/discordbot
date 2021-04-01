package com.github.nsilbernagel.discordbot.audio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

@Component
public class LavaTrackScheduler extends AudioEventAdapter {

  @Nullable
  private AudioTrack currentTrack;

  private final LavaPlayerAudioProvider lavaPlayerAudioProvider;

  private final GatewayDiscordClient discordClient;

  @Getter
  private final BlockingQueue<AudioTrack> queue = new LinkedBlockingDeque<>();

  @Getter
  private final Map<String, AudioRequest> audioRequest = new HashMap<>();

  public LavaTrackScheduler(LavaPlayerAudioProvider lavaPlayerAudioProvider, GatewayDiscordClient discordClient) {
    this.lavaPlayerAudioProvider = lavaPlayerAudioProvider;
    this.discordClient = discordClient;
  }

  /**
   * @param track to queue
   */
  public void queue(AudioTrack track, String requestId) throws LavaPlayerException {
    AudioRequest correspondingAudioRequest = this.audioRequest.get(requestId);

    if(correspondingAudioRequest == null){
      throw new LavaPlayerException("No corresponding AudioRequest found for request id " + requestId);
    }

    correspondingAudioRequest.getTrackList()
        .add(track);

    // set Track Request to being queued so it doesn't get deleted
    correspondingAudioRequest.setStatus(AudioStatus.QUEUED);

    boolean trackImmediatelyStarted = this.lavaPlayerAudioProvider.getPlayer()
        .startTrack(track, true);

    if (trackImmediatelyStarted) {
      return;
    }

    this.queue.offer(track);
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
    Optional<AudioRequest> audioRequest = this.audioRequestByTrack(track);
    if(audioRequest.isEmpty()){
      return;
    }

    // remove the track that just ended from the audio request
    audioRequest.get()
        .getTrackList()
        .remove(track);

    // delete the audio request if it is now empty
    if(audioRequest.get().getTrackList().isEmpty()){
      this.audioRequest.remove(audioRequest.get().getId());
    }

    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
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
    Optional<AudioRequest> audioRequest = this.audioRequestByTrack(track);
    if(audioRequest.isEmpty()){
      return;
    }

    this.setPresencePlayingTrack(track, audioRequest.get().getId());
  }

  private void setPresencePlayingTrack(AudioTrack track, String requestId) {
    this.currentTrack = track;

    this.discordClient
        .updatePresence(Presence.online(Activity.playing(track.getInfo().title)))
        .subscribe();
  }

  @Override
  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
    Optional<AudioRequest> audioRequest = this.audioRequestByTrack(track);
    if(audioRequest.isEmpty()){
      return;
    }

    audioRequest.get()
        .getTaskRequest()
        .getChannel()
        .createMessage("Ich konnte das Audio <" + audioRequest.get().getId() + "> nicht abspielen. Ist es öffentlich?")
        .block();
  }

  private void setPresenceOnline() {
    this.currentTrack = null;

    this.discordClient
        .updatePresence(Presence.online())
        .subscribe();
  }

  private Optional<AudioRequest> audioRequestByTrack(AudioTrack track) {
    return this.audioRequest.values()
        .stream()
        .filter(audioRequest ->
            audioRequest.getTrackList()
                .contains(track)
        )
        .findFirst();
  }
}
