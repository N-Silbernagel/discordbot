package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.TaskRequest;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A request to load an audio source
 */
@Getter
@ToString
@EqualsAndHashCode
public class AudioRequest {
  private final String id;
  @Setter
  private AudioStatus status = AudioStatus.QUEUEING;
  private final TaskRequest taskRequest;
  private final List<AudioTrack> trackList = new ArrayList<>();

  public AudioRequest(String id, TaskRequest taskRequest){
    this.id = id;
    this.taskRequest = taskRequest;
  }
}
