package com.github.nsilbernagel.discordbot.message.impl;

import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.model.KickVoting;
import com.github.nsilbernagel.discordbot.model.Vote;
import com.github.nsilbernagel.discordbot.registries.KickVotingRegistry;

import discord4j.common.util.Snowflake;
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
        User userToKick = this.message.getUserMentions().blockFirst();
        if (this.message.getAuthor().isEmpty()) {
            return;
        }
        User msgAuthor = this.message.getAuthor().get();

        if (userToKick == null || userToKick.isBot()) {
            this.answerMessage("Bitte gebe einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
            return;
        }

        Optional<Snowflake> guildId = this.message.getGuildId();
        if (guildId.isEmpty()) {
            return;
        }

        Optional<Member> memberToKick = userToKick.asMember(guildId.get()).blockOptional();
        if (memberToKick.isEmpty()) {
            return;
        }

        Optional<KickVoting> runningKickVoting = this.registry.getByMember(memberToKick.get());
        if (!runningKickVoting.isPresent()) {
            runningKickVoting = this.registry.createKickVoting(memberToKick.get());
        }

        Vote voteByMsgAuthor = new Vote(msgAuthor, this.message.getTimestamp());
        boolean enoughVotes = runningKickVoting.get().addVote(voteByMsgAuthor);
        if (!enoughVotes) {
            this.answerMessage("Noch " + runningKickVoting.get().remainingVotes() + " Stimmen bis "
                    + runningKickVoting.get().getMemberToKick().getUsername() + " rausgeworfen wird.");
        } else {
            this.registry.getVotings().remove(runningKickVoting.get());
            this.answerMessage(runningKickVoting.get().getMemberToKick().getUsername() + " gekickt.");
        }
    }

    public static String getKeyword() {
        return KEYWORD;
    }
}