package com.github.nsilbernagel.discordbot.guard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExclusiveChannelRepository extends CrudRepository<ExclusiveChannelEntity, Long> {
  @Override
  long count();

  @Override
  Optional<ExclusiveChannelEntity> findById(Long aLong);

  @Override
  <S extends ExclusiveChannelEntity> S save(S entity);
}
