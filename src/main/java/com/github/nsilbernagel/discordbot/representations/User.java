package com.github.nsilbernagel.discordbot.representations;

public class User {
  private final long id;
  private final String name;

  public User(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }
}
