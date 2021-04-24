package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.guild.GuildEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "channel_blacklist")
public class BlackListedChannelEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  private long id;

  @Getter
  @Column(name = "channel_id", unique = true)
  private long channelId;

  @Getter
  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "guild_id", referencedColumnName = "id", unique = true, nullable = false)
  private GuildEntity guild;

  public BlackListedChannelEntity() {}

  public BlackListedChannelEntity(long channelId, GuildEntity guild) {
    this.channelId = channelId;
    this.guild = guild;
  }
}
