package com.github.nsilbernagel.discordbot;

/**
 * Thrown when no Discord bot token property was given in the environment, a
 * app.discord.token propery has to be set in application-local.properties file,
 * which is ignored in git In Prod, this is done through setting the
 * DISCORD_TOKEN env var
 */
public class MissingTokenException extends RuntimeException {
  static final long serialVersionUID = 2L;

  public MissingTokenException() {
    super(
        "Please provide a Discord Bot Token in the app.discord.token property. For development environments, do so by setting it in the application-local.properties file.");
  }
}
