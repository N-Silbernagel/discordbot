package com.github.nsilbernagel.discordbot.vote;

import com.github.nsilbernagel.discordbot.message.MessageTestUtil;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.TaskException;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoteKickTaskTest {
  @Mock
  private VotingRegistry votingRegistry;
  @Mock
  private VoteKickMessageDeleteTask voteKickMessageDeleteTask;
  @Mock
  private VoteKickPlusTask voteKickPlusTask;
  @Mock
  private Guild guild;

  private VoteKickTask voteKickTask;
  private MsgTaskRequest msgTaskRequest;
  private final Snowflake guildId = Snowflake.of(1);

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    voteKickTask = new VoteKickTask(this.votingRegistry, this.voteKickPlusTask, this.voteKickMessageDeleteTask);

    msgTaskRequest = MessageTestUtil.generateMsgTaskRequest();

    when(msgTaskRequest.getMessage().getGuild()).thenReturn(Mono.just(guild));
    when(guild.getId()).thenReturn(guildId);
  }

  @Test
  public void a_user_needs_to_be_mentioned() {
    when(msgTaskRequest.getMessage().getUserMentions()).thenReturn(Flux.empty());

    assertThrows(TaskException.class, () -> {
      voteKickTask.execute(msgTaskRequest);
    });
  }

  // test setup for other test with member mentioned, because I coded it before noticing I dont need it
  public void it_does_something_else() {
    User user = mock(User.class);

    when(msgTaskRequest.getMessage().getUserMentions()).thenReturn(Flux.just(user));

    Member member = mock(Member.class);

    when(user.asMember(eq(guildId))).thenReturn(Mono.just(member));

    voteKickTask.execute(msgTaskRequest);
  }
}