package com.github.nsilbernagel.discordbot.audio;

import java.util.Optional;

import reactor.core.publisher.Flux;

public abstract class SoundsSource<S extends Sound> {
  /**
   * Fetch all the available sounds
   */
  abstract public Flux<S> fetch();

  /**
   * Filter for a sound
   */
  abstract public Optional<S> filter(String query);

  abstract public S random();
}
