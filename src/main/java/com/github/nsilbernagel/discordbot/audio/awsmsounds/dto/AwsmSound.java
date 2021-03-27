package com.github.nsilbernagel.discordbot.audio.awsmsounds.dto;

import com.github.nsilbernagel.discordbot.audio.Sound;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AwsmSound extends Sound {
  private String label;
  private String url;

  public String getSource() {
    return this.url;
  }
}
