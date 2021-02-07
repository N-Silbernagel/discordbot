package com.github.nsilbernagel.discordbot.message.impl;

import com.github.nsilbernagel.discordbot.guard.annotations.SpamRegistry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class SpamHandlerTask extends AbstractMessageTask {

  @Value("${app.guard.spam.enabled:false}")
  private boolean spamProtectionEnabled;

  @Autowired
  private SpamRegistry spamRegistry;

  public boolean canHandle(String command) {
    return this.spamProtectionEnabled;
  }

  public void action() {
    this.getMessage()
        .getAuthorAsMember()
        .flatMap(msgAuthor -> Mono.just(this.spamRegistry.countMemberUp(msgAuthor)))
        .subscribe();
  }
}
