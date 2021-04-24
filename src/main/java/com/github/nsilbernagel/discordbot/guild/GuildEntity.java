package com.github.nsilbernagel.discordbot.guild;

import com.github.nsilbernagel.discordbot.guard.ExclusiveChannelEntity;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "guild")
public class GuildEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  private long id;

  @Getter
  private long dc_id;

  @Getter
  @OneToOne(mappedBy = "guild")
  private ExclusiveChannelEntity exclusiveChannel;

  public GuildEntity() {}

  public GuildEntity(long dc_id) {
    this.dc_id = dc_id;
  }
}
