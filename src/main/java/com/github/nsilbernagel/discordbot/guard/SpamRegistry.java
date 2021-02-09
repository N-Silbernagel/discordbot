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

@Component
public class SpamRegistry {
  private Map<Member, Integer> memberMessageCountMap = new HashMap<Member, Integer>();

  @Value("${app.guard.spam.commands.allowed:3}")
  private Integer commandsPerInterval;

  @Value("${app.guard.spam.reduction.timeout:60000}")
  private Integer reductionInterval;

  @Getter
  @Value("${app.guard.spam.enabled:false}")
  private boolean spamProtectionEnabled;

  /**
   * Count up the users messages counter or register him if not present yet
   *
   * @param user
   *               the user who created the message
   * @return the new number of user messages
   */
  public Integer countMemberUp(Member member) {
    PermissionSet membersPermissions = member.getBasePermissions().block();

    if (membersPermissions.contains(Permission.ADMINISTRATOR)) {
      return 0;
    }

    this.memberMessageCountMap.putIfAbsent(member, 0);

    Integer currentMemberMessageCount = this.memberMessageCountMap.get(member);

    this.memberMessageCountMap.replace(member, ++currentMemberMessageCount);

    this.scheduleReduction(member);

    return currentMemberMessageCount;
  }

  /**
   * Reduce the spam count of a member
   *
   * @param member
   * @return new spam count
   */
  public Integer reduceMemberCount(Member member) {
    Optional<Integer> countForMember = Optional.ofNullable(this.memberMessageCountMap.get(member));

    if (!countForMember.isPresent()) {
      return 0;
    }

    Integer newCount = countForMember.get() - 1;

    this.memberMessageCountMap.replace(member, newCount);

    if (newCount == 0) {
      this.memberMessageCountMap.remove(member);
      return 0;
    }

    return newCount;
  }

  /**
   * Schedule the reduction of a members spam count
   *
   * @param member
   */
  private void scheduleReduction(Member member) {
    Mono.delay(Duration.ofMillis(this.reductionInterval))
        .doOnSuccess(onSuccess -> reduceMemberCount(member))
        .subscribe();
  }

  /**
   * Return if the member in question has exceeded his max bot commands
   *
   * @param memberInQuestion
   * @return wether the user has exceeded the commands thershold
   */
  public boolean memberHasExceededThreshold(Member memberInQuestion) {
    Optional<Integer> countForMember = Optional.ofNullable(this.memberMessageCountMap.get(memberInQuestion));

    if (!countForMember.isPresent()) {
      return false;
    }

    return countForMember.get() >= this.commandsPerInterval;
  }
}
