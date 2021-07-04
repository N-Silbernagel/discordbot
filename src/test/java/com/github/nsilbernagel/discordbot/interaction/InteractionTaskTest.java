package com.github.nsilbernagel.discordbot.interaction;

import discord4j.core.event.domain.InteractionCreateEvent;
import discord4j.core.object.command.Interaction;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

import java.util.Optional;

import static org.mockito.Mockito.*;

class InteractionTaskTest {
  @Test
  public void a_member_without_the_required_permission_may_not_execute_the_task() {
    AdminInteractionTaskStub adminInteractionTaskStub = new AdminInteractionTaskStub();
    InteractionCreateEvent event = mock(InteractionCreateEvent.class);
    Interaction interaction = mock(Interaction.class);

    PublisherProbe<Void> replyMono = PublisherProbe.empty();

    InteractionTaskRequest request = mock(InteractionTaskRequest.class);

    Member restrictedMember = mock(Member.class);
    when(restrictedMember.getBasePermissions()).thenReturn(Mono.empty());

    when(request.getEvent()).thenReturn(event);
    when(event.getInteraction()).thenReturn(interaction);
    when(interaction.getMember()).thenReturn(Optional.of(restrictedMember));
    when(request.getEvent().replyEphemeral(anyString())).thenReturn(replyMono.mono());

    adminInteractionTaskStub.execute(request);
    replyMono.assertWasSubscribed();
  }

  @Test
  public void a_member_with_the_required_permission_may_execute_the_task() {
    AdminInteractionTaskStub adminInteractionTaskStub = new AdminInteractionTaskStub();
    InteractionCreateEvent event = mock(InteractionCreateEvent.class);
    Interaction interaction = mock(Interaction.class);

    PublisherProbe<Void> replyMono = PublisherProbe.empty();

    InteractionTaskRequest request = mock(InteractionTaskRequest.class);

    Member restrictedMember = mock(Member.class);
    when(restrictedMember.getBasePermissions()).thenReturn(Mono.just(PermissionSet.of(Permission.ADMINISTRATOR)));

    when(request.getEvent()).thenReturn(event);
    when(event.getInteraction()).thenReturn(interaction);
    when(interaction.getMember()).thenReturn(Optional.of(restrictedMember));
    when(request.getEvent().replyEphemeral(anyString())).thenReturn(replyMono.mono());

    adminInteractionTaskStub.execute(request);
    replyMono.assertWasNotSubscribed();
  }
}