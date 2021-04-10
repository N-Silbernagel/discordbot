package com.github.nsilbernagel.discordbot;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

public class TestableMono<T> {
  private final AtomicInteger subscribedCount;

  private final Mono<T> mono;

  public TestableMono() {
    this(Mono.empty());
  }

  public TestableMono(Mono<T> mono) {
    this.subscribedCount = new AtomicInteger(0);

    this.mono = mono.doOnSubscribe(unused -> this.subscribedCount.set(this.subscribedCount.get() + 1));
  }

  public Mono<T> getMono() {
    return mono;
  }

  public int getSubscribedCount() {
    return this.subscribedCount.get();
  }

  public boolean wasSubscribedTo(){
    return this.subscribedCount.get() > 0;
  }
}
