package com.github.nsilbernagel.discordbot;

import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestableFlux<T> {
  private final AtomicBoolean subscribedTo;

  private final Flux<T> flux;

  public TestableFlux() {
    this(Flux.empty());
  }

  public TestableFlux(Flux<T> flux) {
    this.subscribedTo = new AtomicBoolean(false);

    this.flux = flux.doOnSubscribe(unused -> this.subscribedTo.set(true));
  }


  public Flux<T> getFlux() {
    return this.flux;
  }

  public boolean wasSubscribedTo(){
    return this.subscribedTo.get();
  }
}
