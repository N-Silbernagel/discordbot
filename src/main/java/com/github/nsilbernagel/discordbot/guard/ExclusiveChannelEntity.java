package com.github.nsilbernagel.discordbot.guard;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "exclusive_channel")
public class ExclusiveChannelEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  private long id;

  @Getter
  @Column(name = "channel_id", unique = true, nullable = false)
  private long channelId;

  @Getter
  @Column(name = "guild_id", unique = true, nullable = false)
  private long guildId;

  public ExclusiveChannelEntity() {}

  public ExclusiveChannelEntity(long channelId, long guildId) {
    this.channelId = channelId;
    this.guildId = guildId;
  }
}
