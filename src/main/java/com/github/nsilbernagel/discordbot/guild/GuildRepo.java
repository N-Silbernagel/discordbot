package com.github.nsilbernagel.discordbot.guild;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GuildRepo extends CrudRepository<GuildEntity, Long> {
  @Override
  Optional<GuildEntity> findById(Long aLong);
}
