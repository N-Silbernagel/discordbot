package com.github.nsilbernagel.discordbot;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Provide the SpringContext for non-spring-managed classes
 */
@Service
public class BeanUtil implements ApplicationContextAware {
  @Getter
  private static ApplicationContext springContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    springContext = applicationContext;
  }
}
