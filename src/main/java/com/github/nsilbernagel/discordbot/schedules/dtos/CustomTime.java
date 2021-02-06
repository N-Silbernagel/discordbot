package com.github.nsilbernagel.discordbot.schedules.dtos;

import java.util.Calendar;

public class CustomTime {
    private int hour;
    private int minute;

    public CustomTime() {
        this.hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        this.minute = Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public String getString() {
        return this.hour + " Uhr";
    }
}
