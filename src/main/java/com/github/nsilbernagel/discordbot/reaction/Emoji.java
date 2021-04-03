package com.github.nsilbernagel.discordbot.reaction;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * Collection of common Emojis used in the app
 */
public enum Emoji {
  BUG("🐛"),
  GUARD("👮‍♂️"),
  QUESTION_MARK("❓"),
  CHECK("✅"),
  PAUSE("⏸"),
  CROSS("❌");

  @Getter
  private final ReactionEmoji.Unicode unicodeEmoji;

  public Mono<Void> reactOn(Message message) {
    return message.addReaction(this.unicodeEmoji);
  }

  Emoji(final String emojiString) {
    this.unicodeEmoji = ReactionEmoji.unicode(emojiString);
  }
}
