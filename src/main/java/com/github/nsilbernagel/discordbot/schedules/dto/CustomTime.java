package com.github.nsilbernagel.discordbot.schedules.dto;

import java.util.Calendar;

import lombok.Getter;

public class CustomTime {
  @Getter
  private int hour;
  @Getter
  private int minute;

  public CustomTime() {
    this.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    this.minute = Calendar.getInstance().get(Calendar.MINUTE);
  }

  public String getString() {
    return this.hour + " Uhr";
  }
}