package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.message.TaskLogicException;
import com.github.nsilbernagel.discordbot.model.KickVoting;
import com.github.nsilbernagel.discordbot.model.Vote;
import com.github.nsilbernagel.discordbot.registries.KickVotingRegistry;

import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class VoteKickTask extends AbstractMessageTask implements IMessageTask {
    private final static String KEYWORD = "votekick";

    private KickVotingRegistry registry = KickVotingRegistry.getInstance();

    public VoteKickTask(Message message, CommandPattern pattern) {
        super(message, pattern);
    }

    @Override
    public void execute() {
        User msgAuthor = this.message.getAuthor().orElseThrow(() -> new TaskLogicException());

        User userToKick = this.message.getUserMentions().blockFirst();
        if (userToKick == null || userToKick.isBot()) {
            throw new TaskLogicException("Bitte gebe einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
        }

        Snowflake guildId = this.message.getGuildId().orElseThrow(() -> new TaskLogicException());

        Member memberToKick = userToKick.asMember(guildId).blockOptional()
                .orElseThrow(() -> new TaskLogicException(userToKick.getUsername() + "ist kein Member dieses Server."));

        // member cannot be kicked if it is not in voice channel
        VoiceState membersVoiceState = memberToKick.getVoiceState().blockOptional().orElseThrow(
                () -> new TaskLogicException(memberToKick.getDisplayName() + " ist nicht in einem voice Channel."));
        membersVoiceState.getChannel().blockOptional().orElseThrow(
                () -> new TaskLogicException(memberToKick.getDisplayName() + " ist nicht in einem voice Channel."));

        Optional<KickVoting> runningKickVoting = this.registry.getByMember(memberToKick);
        if (!runningKickVoting.isPresent()) {
            runningKickVoting = this.registry.createKickVoting(memberToKick);
        }

        Vote voteByMsgAuthor = new Vote(msgAuthor, this.message.getTimestamp());

        boolean enoughVotes = runningKickVoting.get().addVote(voteByMsgAuthor);

        if (!enoughVotes) {
            this.answerMessage("Noch " + runningKickVoting.get().remainingVotes() + " Stimmen bis "
                    + memberToKick.getDisplayName() + " rausgeworfen wird.");
        } else {
            this.registry.getVotings().remove(runningKickVoting.get());
            this.answerMessage(memberToKick.getDisplayName() + " gekickt.");
        }
    }

    public static String getKeyword() {
        return KEYWORD;
    }
}