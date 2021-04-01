package com.github.nsilbernagel.discordbot.audio;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskRequest;
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
  private final MsgTaskRequest taskRequest;
  private final List<AudioTrack> trackList = new ArrayList<>();

  public AudioRequest(String id, MsgTaskRequest taskRequest){
    this.id = id;
    this.taskRequest = taskRequest;
  }
}
