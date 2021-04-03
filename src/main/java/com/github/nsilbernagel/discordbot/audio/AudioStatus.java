package com.github.nsilbernagel.discordbot.audio;

import lombok.Getter;

/**
 * Status of an AudioRequest
 */
public enum AudioStatus {
  QUEUEING(true),
  QUEUED(false);

  @Getter
  private final boolean deletable;

  AudioStatus(boolean deletable){
    this.deletable = deletable;
  }
}
