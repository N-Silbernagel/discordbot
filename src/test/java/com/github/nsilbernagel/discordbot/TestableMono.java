package com.github.nsilbernagel.discordbot;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestableMono<T> {
  private final AtomicBoolean subscribedTo;

  private final Mono<T> mono;

  public TestableMono() {
    this(Mono.empty());
  }

  public TestableMono(Mono<T> mono) {
    this.subscribedTo = new AtomicBoolean(false);

    this.mono = mono.doOnSubscribe(unused -> this.subscribedTo.set(true));
  }

  public Mono<T> getMono() {
    return mono;
  }

  public boolean wasSubscribedTo(){
    return this.subscribedTo.get();
  }
}
