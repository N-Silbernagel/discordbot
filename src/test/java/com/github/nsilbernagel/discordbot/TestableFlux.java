package com.github.nsilbernagel.discordbot;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestableFlux<T> {
  private final AtomicBoolean subscribedTo;

  private final Flux<T> flux;

  private TestableFlux(Class<T> klass) {
    this.subscribedTo = new AtomicBoolean(false);

    this.flux = Flux.empty().doOnSubscribe(unused -> this.subscribedTo.set(true)).cast(klass);
  }


  public Flux<T> getFlux() {
    return this.flux;
  }

  public boolean wasSubscribedTo(){
    return this.subscribedTo.get();
  }

  public static <M> TestableFlux<M> forClass(Class<M> klass){
    return new TestableFlux<>(klass);
  }
}
