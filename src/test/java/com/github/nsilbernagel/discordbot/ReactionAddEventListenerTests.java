package com.github.nsilbernagel.discordbot;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.task.MemberMissingOrBotException;
import com.github.nsilbernagel.discordbot.reaction.ReactionAddEventListener;
import com.github.nsilbernagel.discordbot.reaction.ReactionToTaskHandler;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReactionAddEventListenerTests {
  @Mock
  private ReactionToTaskHandler reactionToTaskHandlerMock;
  @Mock
  private ChannelBlacklist channelBlacklistMock;
  @Mock
  private ExclusiveBotChannel exclusiveBotChannelMock;
  @Mock
  private ReactionAddEvent reactionAddEventMock;
  @Mock
  private Member memberMock;

  @BeforeEach
  public void setUp(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void it_does_not_handle_its_own_reactions() {
    ReactionAddEventListener reactionAddEventListener = new ReactionAddEventListener(this.reactionToTaskHandlerMock, this.channelBlacklistMock, this.exclusiveBotChannelMock);

    when(this.memberMock.isBot()).thenReturn(true);
    when(reactionAddEventMock.getMember()).thenReturn(Optional.of(this.memberMock));

    assertThrows(MemberMissingOrBotException.class, () -> reactionAddEventListener.execute(reactionAddEventMock));
  }
}
