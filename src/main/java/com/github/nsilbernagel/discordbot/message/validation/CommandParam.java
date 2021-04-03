package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.task.validation.ValidationRule;
import lombok.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Objects;

@Getter
@ToString
public class CommandParam {
  /**
   * Command param's raw value
   */
  private final String raw;

  private final ConversionService conversionService = DefaultConversionService.getSharedInstance();

  public CommandParam(String raw){
    this.raw = raw;
  }

  public <R extends ValidationRule> CommandParam is(R rule, String errorMessage){
    boolean valid = rule.validate(this);

    if (!valid){
      throw new MessageValidationException(errorMessage);
    }
    return this;
  }

  public <T> T as(Class<T> klass){
    return conversionService.convert(this.raw, klass);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CommandParam that = (CommandParam) o;
    return Objects.equals(raw, that.raw);
  }

  @Override
  public int hashCode() {
    return Objects.hash(raw);
  }
}
