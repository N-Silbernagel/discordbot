package com.github.nsilbernagel.discordbot.listeners;

import com.github.nsilbernagel.discordbot.message.TaskException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;

import java.util.Arrays;

@Component
public abstract class EventListener<E extends Event> {
  @Autowired
  private GatewayDiscordClient discordClient;
  
  @Autowired
  private Environment env;

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
   * @param checkedException the thrown exception to handle
   */
  abstract protected void onCheckedException(TaskException checkedException);

  /**
   * Do something in the case of an unchecked exception being thrown in the execute method
   * These exceptions will be caught in prod
   * @param uncheckedException the thrown exception to handle
   */
  abstract protected void onUncheckedException(Exception uncheckedException);

  private void executeWithExceptionHandling(E event) {
    try {
      this.execute(event);
    } catch (TaskException checkedException) {
      this.onCheckedException(checkedException);
    } catch (Exception uncheckedException) {
      this.onUncheckedException(uncheckedException);
      if(Arrays.stream(this.env.getActiveProfiles()).anyMatch(activeProfile -> activeProfile.equalsIgnoreCase("prod"))) {
        // if we are in a prod environment, we don't want the app to crash, just print the stack trace to console silently
        uncheckedException.printStackTrace();
        return;
      }
      // in dev throw the exception so we know of error instantly
      throw uncheckedException;
    }
  }

  /**
   * Register a D4J Event listener
   */
  public void register() {
    this.discordClient.on(this.getEventType()).subscribe(this::executeWithExceptionHandling);
  }
}
