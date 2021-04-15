package com.github.nsilbernagel.discordbot.guard;

import com.github.nsilbernagel.discordbot.TestableMono;
import discord4j.core.object.entity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageFilterTest {
  @Spy
  private MessageFilter messageFilter;
  @Mock
  private Message message;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void it_filters_out_messages_by_a_regex() {
    when(this.messageFilter.getRegexFilters()).thenReturn(List.of(".*abc.*"));
    when(this.message.getContent()).thenReturn("test abc 123");

    TestableMono<Void> messageDeleteMono = new TestableMono<>();
    when(this.message.delete()).thenReturn(messageDeleteMono.getMono());

    this.messageFilter.execute(this.message);

    assertTrue(messageDeleteMono.wasSubscribedTo());
  }

  @Test
  public void it_does_not_filter_out_messages_that_dont_contain_regex(){
    when(this.messageFilter.getRegexFilters()).thenReturn(List.of("abc"));
    when(this.message.getContent()).thenReturn("test 123");

    TestableMono<Void> messageDeleteMono = new TestableMono<>();
    when(this.message.delete()).thenReturn(messageDeleteMono.getMono());

    this.messageFilter.execute(this.message);

    assertFalse(messageDeleteMono.wasSubscribedTo());
  }
}