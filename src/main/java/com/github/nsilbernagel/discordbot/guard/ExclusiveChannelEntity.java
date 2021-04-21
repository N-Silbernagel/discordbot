package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.guild.GuildEntity;
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
  private long channel_id;

  @Getter
  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "guild_id", referencedColumnName = "id", unique = true, nullable = false)
  private GuildEntity guild;

  public ExclusiveChannelEntity() {}

  public ExclusiveChannelEntity(long channel_id, GuildEntity guild) {
    this.channel_id = channel_id;
    this.guild = guild;
  }
}
