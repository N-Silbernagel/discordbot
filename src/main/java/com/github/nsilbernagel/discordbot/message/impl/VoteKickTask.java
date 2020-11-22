package com.github.nsilbernagel.discordbot.message.impl;

import java.time.Instant;
import java.util.Optional;

import com.github.nsilbernagel.discordbot.message.CommandPattern;
import com.github.nsilbernagel.discordbot.message.IMessageTask;
import com.github.nsilbernagel.discordbot.model.KickVoting;
import com.github.nsilbernagel.discordbot.model.Vote;
import com.github.nsilbernagel.discordbot.registries.KickVotingRegistry;

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
        User firstMention = this.message.getUserMentions().blockFirst();
        addVoteToRunningVoting(firstMention, this.message.getAuthor(), this.message.getTimestamp());
    }

    private void addVoteToRunningVoting(User userToKick, Optional<User> msgAuthor, Instant time) {
        if (!msgAuthor.isPresent()) {
            return;
        }

        if (userToKick == null) {
            this.answerMessage("Bitte gebe einen Nutzer an, indem du ihn mit '@NUTZER' markierst.");
            return;
        }

        Optional<KickVoting> runningKickVoting = this.registry.getByUser(userToKick);
        if (!runningKickVoting.isPresent()) {
            runningKickVoting = this.registry.createKickVoting(userToKick);
        }

        Vote voteByMsgAuthor = new Vote(msgAuthor.get(), time);
        boolean enoughVotes = runningKickVoting.get().addVote(voteByMsgAuthor);
        if (!enoughVotes) {
            this.answerMessage("Noch " + runningKickVoting.get().remainingVotes() + " Stimmen bis "
                    + runningKickVoting.get().getUserToKick().getUsername() + " rausgeworfen wird.");
        } else {
            this.registry.getVotings().remove(runningKickVoting.get());
            this.answerMessage(runningKickVoting.get().getUserToKick().getUsername() + " gekickt.");
        }
    }

    public static String getKeyword() {
        return KEYWORD;
    }
}