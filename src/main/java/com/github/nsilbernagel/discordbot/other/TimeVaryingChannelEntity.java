package com.github.nsilbernagel.discordbot.other;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Table(name = "time_varying_channel")
@EqualsAndHashCode
public class TimeVaryingChannelEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  private long id;

  @Getter
  @Setter
  @Column(name = "channel_id", unique = true, nullable = false)
  private long channelId;

  @Getter
  @Column(name = "guild_id", unique = true, nullable = false)
  private long guildId;

  @Getter
  @Setter
  @Column(name = "default_name", nullable = false)
  private String defaultName;

  @Setter
  @Column(name = "morning_name")
  private String morningName;

  @Setter
  @Column(name = "noon_name")
  private String noonName;

  @Setter
  @Column(name = "evening_name")
  private String eveningName;

  public Optional<String> getMorningName() {
    return Optional.ofNullable(morningName);
  }

  public Optional<String> getNoonName() {
    return Optional.ofNullable(noonName);
  }

  public Optional<String> getEveningName() {
    return Optional.ofNullable(eveningName);
  }

  public TimeVaryingChannelEntity() {}

  public TimeVaryingChannelEntity(long channelId, long guildId, String defaultName) {
    this.channelId = channelId;
    this.guildId = guildId;
    this.defaultName = defaultName;
  }
}
