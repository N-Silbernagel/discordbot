package com.github.nsilbernagel.discordbot.guard;

import org.springframework.data.repository.CrudRepository;

public interface ChannelBlacklistRepo extends CrudRepository<BlackListedChannelEntity, Long> {
}
