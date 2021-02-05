package com.github.nsilbernagel.discordbot.audio;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import org.springframework.stereotype.Component;

import discord4j.voice.AudioProvider;

@Component("LavaPlayerAudioProvider")
public class LavaPlayerAudioProvider extends AudioProvider {

  private final AudioPlayer player;
  private final MutableAudioFrame frame = new MutableAudioFrame();

  public LavaPlayerAudioProvider() {
    // Allocate a ByteBuffer for Discord4J's AudioProvider to hold audio data
    // for Discord
    super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));

    // Set LavaPlayer's MutableAudioFrame to use the same buffer as the one we
    // just allocated
    frame.setBuffer(getBuffer());

    // Creates AudioPlayer instances and translates URLs to AudioTrack instances
    final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    // This is an optimization strategy that Discord4J can utilize.
    // It is not important to understand
    playerManager.getConfiguration()
        .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

    // Allow playerManager to parse remote sources like YouTube links
    AudioSourceManagers.registerRemoteSources(playerManager);

    // Create an AudioPlayer so Discord4J can receive audio data
    this.player = playerManager.createPlayer();
  }

  @Override
  public boolean provide() {
    // AudioPlayer writes audio data to its AudioFrame
    final boolean didProvide = player.provide(frame);
    // If audio was provided, flip from write-mode to read-mode
    if (didProvide) {
      getBuffer().flip();
    }

    return didProvide;
  }
}
