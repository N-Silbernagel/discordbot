package com.github.nsilbernagel.discordbot.reaction;

import com.github.nsilbernagel.discordbot.guard.ChannelBlacklist;
import com.github.nsilbernagel.discordbot.guard.ExclusiveBotChannel;
import com.github.nsilbernagel.discordbot.task.MemberMissingOrBotException;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReactionAddEventListenerTests {
  @Mock
  private ChannelBlacklist channelBlacklistMock;
  @Mock
  private ExclusiveBotChannel exclusiveBotChannelMock;
  @Mock
  private ReactionAddEvent reactionAddEventMock;
  @Mock
  private Member memberMock;
  @Mock
  private ReactionTask reactionTaskMock;
  @Mock
  private GatewayDiscordClient discordClientMock;
  @Mock
  private Environment envMock;

  @Test
  public void it_does_not_handle_its_own_reactions() {
    ReactionAddEventListener reactionAddEventListener = new ReactionAddEventListener(this.channelBlacklistMock, this.exclusiveBotChannelMock, List.of(this.reactionTaskMock), this.discordClientMock, this.envMock);

    when(this.memberMock.isBot()).thenReturn(true);
    when(reactionAddEventMock.getMember()).thenReturn(Optional.of(this.memberMock));

    assertThrows(MemberMissingOrBotException.class, () -> reactionAddEventListener.execute(reactionAddEventMock));
  }
}
