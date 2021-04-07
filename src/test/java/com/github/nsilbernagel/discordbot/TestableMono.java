package com.github.nsilbernagel.discordbot;

import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestableMono<T> {
  private final AtomicBoolean subscribedTo;

  private final Mono<T> mono;

  private TestableMono(Class<T> klass) {
    this.subscribedTo = new AtomicBoolean(false);

    this.mono = Mono.empty().doOnSubscribe(unused -> this.subscribedTo.set(true)).cast(klass);
  }


  public Mono<T> getMono() {
    return mono;
  }

  public boolean wasSubscribedTo(){
    return this.subscribedTo.get();
  }

  public static <M> TestableMono<M> forClass(Class<M> klass){
    return new TestableMono<>(klass);
  }
}
