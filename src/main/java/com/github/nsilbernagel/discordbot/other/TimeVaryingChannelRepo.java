package com.github.nsilbernagel.discordbot.other;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TimeVaryingChannelRepo extends CrudRepository<TimeVaryingChannelEntity, Long> {
  Optional<TimeVaryingChannelEntity> findByguildId(Long guildId);
}
