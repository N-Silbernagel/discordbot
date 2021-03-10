package com.github.nsilbernagel.discordbot.communication;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Handles communication with members
 */
@Component
public class Communicator {
    public final static String EMOJI_BUG = "🐛";
    public final static String EMOJI_GUARD = "👮‍♂️";
    public final static String EMOJI_QUESTION_MARK = "❓";
    public final static String EMOJI_CHECK = "✅";
    public final static String EMOJI_CROSS = "❌";

    /**
     *
     * @param message the message to react to
     */
    public Mono<Void> react(Message message, String unicodeEmojiString) {
        return message.addReaction(
                ReactionEmoji.unicode(unicodeEmojiString)
        );
    }
}
