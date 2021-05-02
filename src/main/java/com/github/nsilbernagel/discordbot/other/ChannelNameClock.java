package com.github.nsilbernagel.discordbot.other;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.VoiceChannel;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
@Profile("prod")
public class ChannelNameClock {
  public final static int MORNING_BEGIN = 6;
  public final static int NOON_BEGIN = 12;
  public final static int EVENING_BEGIN = 18;

  private final GatewayDiscordClient discordClient;
  private final TimeVaryingChannelRepo repo;

  public ChannelNameClock(GatewayDiscordClient discordClient, TimeVaryingChannelRepo repo) {
    this.discordClient = discordClient;
    this.repo = repo;
  }

  @PostConstruct
  public void init() {
    this.changeChannelNames();
  }

  @Scheduled(cron = "0 0 */1 * * ?")
  public void changeChannelNames() {
    repo.findAll().forEach(this::setChannelName);
  }

  @PreDestroy
  public void shutdown() {
    repo.findAll().forEach(this::resetChannelName);
  }

  private void setChannelName(TimeVaryingChannelEntity entity) {
    Snowflake channelId = Snowflake.of(entity.getChannelId());
    VoiceChannel channel = (VoiceChannel) discordClient.getChannelById(channelId).block();

    assert channel != null;
    this.timeVaryName(channel, entity);
  }

  private void timeVaryName(VoiceChannel channel, TimeVaryingChannelEntity entity) {
    int hour = this.getHourOfDay();

    if (hour >= MORNING_BEGIN && hour < NOON_BEGIN) {
      channel.edit(spec -> spec.setName(hourString(hour) + " |  " + entity.getMorningName().orElse(entity.getDefaultName()))).block();
    } else if (hour >= NOON_BEGIN && hour < EVENING_BEGIN) {
      channel.edit(spec -> spec.setName(hourString(hour) + " |  " + entity.getNoonName().orElse(entity.getDefaultName()))).block();
    } else {
      channel.edit(spec -> spec.setName(hourString(hour) + " |  " + entity.getEveningName().orElse(entity.getDefaultName()))).block();
    }
  }

  public int getHourOfDay() {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
  }

  private static String hourString(int hourOfDay) {
    return hourOfDay + " Uhr";
  }

  private void resetChannelName(TimeVaryingChannelEntity entity) {
    VoiceChannel channel = (VoiceChannel) discordClient.getChannelById(Snowflake.of(entity.getChannelId())).block();

    assert channel != null;
    channel.edit(spec -> spec.setName(entity.getDefaultName())).block();
  }

}
