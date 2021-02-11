package com.github.nsilbernagel.discordbot.validation.rules;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class Required extends AbstractValidationRule {

  public Class<? extends Annotation> handlesAnnotation() {
    return com.github.nsilbernagel.discordbot.validation.rules.annotations.Required.class;
  };

  public boolean validate(Optional<String> value) {
    return value.isPresent();
  }
}
