package com.github.nsilbernagel.discordbot.guard;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChannelBlacklistRepo extends CrudRepository<BlackListedChannelEntity, Long> {
  Optional<BlackListedChannelEntity> findByChannelId(Long channelId);
}
