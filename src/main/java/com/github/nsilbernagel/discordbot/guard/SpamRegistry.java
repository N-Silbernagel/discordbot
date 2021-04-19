package com.github.nsilbernagel.discordbot.guard;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import lombok.Getter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class SpamRegistry {
  private final Map<Member, Integer> memberMessageCountMap = new HashMap<>();

  @Value("${app.guard.spam.commands.allowed:3}")
  private Integer commandsPerInterval;

  @Value("${app.guard.spam.reduction.timeout:60000}")
  private Integer reductionInterval;

  @Getter
  @Value("${app.guard.spam.enabled:false}")
  private boolean enabled;

  /**
   * Count up the users messages counter or register him if not present yet
   *
   * @param member the member who created the message
   */
  public void countMemberUp(Member member) throws AssertionError {
    PermissionSet membersPermissions = member.getBasePermissions().block();

    assert membersPermissions != null;

    if (membersPermissions.contains(Permission.ADMINISTRATOR)) {
      return;
    }

    this.memberMessageCountMap.putIfAbsent(member, 0);

    Integer currentMemberMessageCount = this.memberMessageCountMap.get(member);

    this.memberMessageCountMap.replace(member, ++currentMemberMessageCount);

    this.scheduleReduction(member);

  }

  /**
   * Reduce the spam count of a member
   *
   */
  public void reduceMemberCount(Member member) {
    Optional<Integer> countForMember = Optional.ofNullable(this.memberMessageCountMap.get(member));

    if (countForMember.isEmpty()) {
      return;
    }

    int newCount = countForMember.get() - 1;

    this.memberMessageCountMap.replace(member, newCount);

    if (newCount == 0) {
      this.memberMessageCountMap.remove(member);
    }

  }

  /**
   * Schedule the reduction of a members spam count
   */
  private void scheduleReduction(Member member) {
    Mono.delay(Duration.ofMillis(this.reductionInterval), Schedulers.single())
        .doOnSuccess(onSuccess -> reduceMemberCount(member))
        .subscribe();
  }

  /**
   * Return if the member in question has exceeded his max bot commands
   *
   * @return if the user has exceeded the commands threshold
   */
  public boolean memberHasExceededThreshold(Member memberInQuestion) {
    Optional<Integer> countForMember = Optional.ofNullable(this.memberMessageCountMap.get(memberInQuestion));

    return countForMember.filter(commandsByMemberInInterval -> commandsByMemberInInterval >= this.commandsPerInterval).isPresent();
  }
}
