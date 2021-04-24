package com.github.nsilbernagel.discordbot.guild;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GuildRepo extends CrudRepository<GuildEntity, Long> {
  Optional<GuildEntity> findBydcId(Long dcId);
}
