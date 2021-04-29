package com.github.nsilbernagel.discordbot.guard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExclusiveChannelRepository extends CrudRepository<ExclusiveChannelEntity, Long> {
  Optional<ExclusiveChannelEntity> findByguildId(Long guildId);
}
