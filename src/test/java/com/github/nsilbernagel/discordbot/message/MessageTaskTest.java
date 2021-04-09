package com.github.nsilbernagel.discordbot.message;

import com.github.nsilbernagel.discordbot.TestableMono;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.PermissionSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageTaskTest {
  @Spy
  private final MsgTaskRequest msgTaskRequest = MessageTestUtil.generateMsgTaskRequest();

  private final TestableMono<Void> guardReactionMono = new TestableMono<>();
  private final PermissionSet emptyPermissionSet = PermissionSet.none();
  private final Mono<PermissionSet> permissionSetMono = Mono.just(emptyPermissionSet);

  @BeforeEach
  public void setUp(){
    MockitoAnnotations.initMocks(this);

    when(msgTaskRequest.getAuthor().getBasePermissions()).thenReturn(permissionSetMono);

    when(msgTaskRequest.getMessage().addReaction(any(ReactionEmoji.Unicode.class))).thenReturn(guardReactionMono.getMono());
  }

  @Test
  public void every_user_may_trigger_tasks_if_no_permission_is_required() {
    MessageTask unrestrictedMessageTask = spy(new MessageTask() {
      @Override
      protected void action(MsgTaskRequest taskRequest) {
      }

      @Override
      public boolean canHandle(String command) {
        return false;
      }
    });

    unrestrictedMessageTask.execute(msgTaskRequest);

    verify(unrestrictedMessageTask).action(msgTaskRequest);
  }

  @Test
  public void a_user_who_doesnt_have_the_required_permissions_may_not_execute_the_task(){
    AdminMessageTaskStub adminMessageTaskStub = spy(new AdminMessageTaskStub());

    when(msgTaskRequest.getMessage().addReaction(any(ReactionEmoji.Unicode.class))).thenReturn(guardReactionMono.getMono());

    verify(adminMessageTaskStub, times(0)).action(msgTaskRequest);
  }

  @Test
  public void it_reacts_with_a_guard_emoji_when_a_user_does_not_have_the_permissions(){
    AdminMessageTaskStub adminMessageTaskStub = spy(new AdminMessageTaskStub());

    adminMessageTaskStub.execute(msgTaskRequest);

    assertTrue(this.guardReactionMono.wasSubscribedTo());
  }
}