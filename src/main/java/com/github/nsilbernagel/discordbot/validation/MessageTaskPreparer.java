package com.github.nsilbernagel.discordbot.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.nsilbernagel.discordbot.message.MessageTask;
import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.validation.rules.ValidationRule;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component
public class MessageTaskPreparer {

  private final List<ValidationRule<? extends Annotation>> validationRules;

  private ConversionService conversionService;

  public MessageTaskPreparer(List<ValidationRule<? extends Annotation>> validationRules) {
    this.validationRules = validationRules;
  }

  /**
   * Prepare a message task, validate and map the command parameters to the
   * corresponding fields in the message task
   */
  public void execute(MessageTask messageTask, MsgTaskRequest msgTaskRequest) {
    // prepare the message tasks fields annotated as CommandParam
    Arrays.stream(messageTask.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(CommandParam.class))
        .forEach((field) -> this.prepareCommandParamField(field, messageTask, msgTaskRequest));
  }

  /**
   * validate and map one command param fields
   */
  private void prepareCommandParamField(Field commandParamField, MessageTask messageTask, MsgTaskRequest msgTaskRequest) throws IllegalArgumentException {
    Arrays.stream(commandParamField.getAnnotations()).filter(fieldValidationAnnotation ->
        this.validationRules.stream().anyMatch(validationRule ->
            validationRule.getCorrespondingAnnotation().equals(fieldValidationAnnotation.annotationType())
        )
    )
        .forEach(fieldValidationAnnotation -> this.validateFieldAccordingToAnnotation(commandParamField, fieldValidationAnnotation, msgTaskRequest));

    // set the actual value of the field to that given to the command as a param
    commandParamField.setAccessible(true);

    int commandParamIndex = commandParamField.getAnnotation(CommandParam.class).pos();

    // a command param can go over many, space separated, params, which means it is
    // going to be a list
    int commandRange = this.getCommandParamRange(commandParamField.getAnnotation(CommandParam.class), msgTaskRequest);

    if (commandRange == 1) {
      this.setFieldValue(messageTask, commandParamField, this.getFieldValueFromCommandParam(commandParamIndex, msgTaskRequest));
    } else {
      List<Object> newFieldValue = new ArrayList<>(commandRange);
      for (int offset = 0; offset < commandRange; offset++) {
        newFieldValue.add(this.getFieldValueFromCommandParam(commandParamIndex + offset, msgTaskRequest));
      }
      this.setFieldValue(messageTask, commandParamField, newFieldValue);
    }
  }

  private void setFieldValue(MessageTask messageTask, Field commandParamField, Object value) {
    try {
      commandParamField.set(
          messageTask,
          this.conversionService.convert(
              value,
              commandParamField.getType()));
    } catch (IllegalAccessException e) {
      // don't need to handle this as we set it accessible beforehand
    }
  }

  private Object getFieldValueFromCommandParam(int commandParamIndex, MsgTaskRequest msgTaskRequest) {
    if (msgTaskRequest.getCommandParameters().size() > commandParamIndex) {
      return msgTaskRequest.getCommandParameters()
          .get(commandParamIndex);
    } else {
      return null;
    }
  }

  /**
   * validate a command param field according to its validation rule annotations
   *
   * @param commandParamField         the field annotated with commandParam
   * @param fieldValidationAnnotation the validation annotation to validate the command param against
   */
  private void validateFieldAccordingToAnnotation(Field commandParamField, Annotation fieldValidationAnnotation, MsgTaskRequest msgTaskRequest)
      throws MessageValidationException, IllegalArgumentException {
    Optional<ValidationRule<? extends Annotation>> validationRuleOptional = this.validationRules.stream()
        .filter(validationRule -> validationRule.getCorrespondingAnnotation()
            .equals(fieldValidationAnnotation.annotationType()))
        .findFirst();

    int commandIndex = commandParamField.getAnnotation(CommandParam.class).pos();
    // a command param can go over many, space separated, params, which means it is
    // going to be a list
    int commandRange = this.getCommandParamRange(commandParamField.getAnnotation(CommandParam.class), msgTaskRequest);

    for (int offset = 0; offset < commandRange; offset++) {
      Optional<String> commandParamValue;
      if (msgTaskRequest.getCommandParameters().size() >= (commandIndex + offset + 1)) {
        commandParamValue = Optional.ofNullable(
            msgTaskRequest.getCommandParameters()
                .get(commandIndex + offset));
      } else {
        commandParamValue = Optional.empty();
      }

      if (validationRuleOptional.isEmpty()) {
        return;
      }

      validationRuleOptional.get()
          .validate(commandParamValue, commandParamField);
    }
  }

  private int getCommandParamRange(CommandParam commandParamAnnotation, MsgTaskRequest msgTaskRequest) throws IllegalArgumentException {
    int range = commandParamAnnotation.range();
    if (range < 1) {
      throw new IllegalArgumentException("Command Param range cannot be smaller than 1.");
    }

    // reduce the range to only be as big as the number of command params at max
    // as we are using Int.MAX_VALUE for infinity and generating an arraylist of
    // that size
    // anything else will result in outOfMemory exception D:
    range = Math.min(range, msgTaskRequest.getCommandParameters().size());

    return range;
  }

  @PostConstruct
  private void setUpConversionService() {
    this.conversionService = new DefaultConversionService();
  }
}
