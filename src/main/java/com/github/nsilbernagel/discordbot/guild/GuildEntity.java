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
  @Column(name = "dc_id", unique = true)
  private long dcId;

  @Getter
  @OneToOne(mappedBy = "guild")
  private ExclusiveChannelEntity exclusiveChannel;

  public GuildEntity() {}

  public GuildEntity(long dcId) {
    this.dcId = dcId;
  }
}
