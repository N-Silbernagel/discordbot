package com.github.nsilbernagel.discordbot.listener;

import com.github.nsilbernagel.discordbot.task.TaskException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;

public abstract class EventListener<E extends Event> {
  private final GatewayDiscordClient discordClient;

  private final Environment env;

  public EventListener(GatewayDiscordClient discordClient, Environment env) {
    this.discordClient = discordClient;
    this.env = env;
  }

  /**
   * Get type of the discord4j event the listener listens for
   */
  abstract public Class<E> getEventType();

  /**
   * The action to perform when the target event was fired
   */
  abstract public void execute(E event);

  /**
   * Handle task exceptions in the case of one being thrown in the execute method
   *
   * @param checkedException the thrown exception to handle
   */
  protected void onCheckedException(TaskException checkedException) {
    // no default error handling
  }

  /**
   * Do something in the case of an unchecked exception being thrown in the execute method
   * These exceptions will be caught in prod
   *
   * @param uncheckedException the thrown exception to handle
   */
  protected void onUncheckedException(Exception uncheckedException) {
    // no default error handling
  }

  private void executeWithExceptionHandling(E event) {
    try {
      this.execute(event);
    } catch (TaskException checkedException) {
      this.onCheckedException(checkedException);
    } catch (Exception uncheckedException) {
      this.onUncheckedException(uncheckedException);
      if (Arrays.stream(this.env.getActiveProfiles()).anyMatch(activeProfile -> activeProfile.equalsIgnoreCase("prod"))) {
        // if we are in a prod environment, we don't want the app to crash, just print the stack trace to console silently
        uncheckedException.printStackTrace();
      } else {
        // in dev throw the exception so we know of error instantly
        throw uncheckedException;
      }
    }
  }

  /**
   * Execute the task on bounded elastic scheduler, thus executing it asynchronously
   */
  private Mono<E> asyncExecuteWithExceptionHandling(E event) {
    return Mono.fromCallable(() -> {
      this.executeWithExceptionHandling(event);
      return event;
    })
        .subscribeOn(Schedulers.boundedElastic());
  }

  /**
   * Register a D4J Event listener
   */
  public void register() {
    this.discordClient.on(this.getEventType())
        .flatMap(this::asyncExecuteWithExceptionHandling)
        .subscribe();
  }
}
