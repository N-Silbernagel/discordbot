package com.github.nsilbernagel.discordbot;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class TestableMonoTest {
  @Test
  public void it_reports_if_a_mono_has_been_subscribed_to(){
    TestableMono<Void> testableMono = new TestableMono<>();

    testableMono.getMono().subscribe();

    assertTrue(testableMono.wasSubscribedTo());
  }

  @Test
  public void it_reports_if_a_mono_has_been_blocked(){
    TestableMono<Integer> testableMono = new TestableMono<>(Mono.just(5));

    testableMono.getMono().block();

    assertTrue(testableMono.wasSubscribedTo());
  }

  @Test
  public void it_reports_if_a_mono_has_not_been_subscribed_to(){
    TestableMono<Void> testableMono = new TestableMono<>();

    assertFalse(testableMono.wasSubscribedTo());
  }
}