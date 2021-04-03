package com.github.nsilbernagel.discordbot.message.validation;

import com.github.nsilbernagel.discordbot.message.MsgTaskRequest;
import com.github.nsilbernagel.discordbot.task.validation.Validator;
import com.github.nsilbernagel.discordbot.task.validation.rules.ValidationRule;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MessageTaskValidator implements Validator<MsgTaskRequest> {
  private final List<ValidationRule<? extends Annotation>> validationRules;

  public MessageTaskValidator(List<ValidationRule<? extends Annotation>> validationRules) {
    this.validationRules = validationRules;
  }

  public boolean validate(MsgTaskRequest request) throws MessageValidationException{
    List<Field> fieldsAnnotatedWithCommandParam = this.commandParamAnnotatedFields(request);

    fieldsAnnotatedWithCommandParam.forEach(field -> {
      this.validateCommandParam(field, request);
      this.setCommandParamFieldValue(field, request);
    });

    return true;
  }

  private void validateCommandParam(Field field, MsgTaskRequest msgTaskRequest) throws MessageValidationException{
    Arrays.stream(field.getAnnotations()).forEach(fieldValidationAnnotation ->
        this.validationRules.forEach(validationRule -> {
          if (validationRule.getCorrespondingAnnotation().equals(fieldValidationAnnotation.annotationType())) {
            this.validateFieldAccordingToAnnotation(field, validationRule, msgTaskRequest);
          }
        }
    ));
  }

  private void validateFieldAccordingToAnnotation(Field field, ValidationRule<?> validationRule, MsgTaskRequest msgTaskRequest) throws MessageValidationException{
    int commandParamIndex = field.getAnnotation(CommandParam.class).pos();

    Optional<String> commandParamValue = Optional.ofNullable(msgTaskRequest.getCommandParameters().get(commandParamIndex));

    validationRule.validate(commandParamValue, field);
  }

  private List<Field> commandParamAnnotatedFields(MsgTaskRequest request) {
    Field[] requestFields = request.getClass().getFields();

    return Arrays.stream(requestFields).filter(field ->
        field.getAnnotation(CommandParam.class) != null
    )
        .collect(Collectors.toList());
  }

  private void setCommandParamFieldValue(Field field, MsgTaskRequest request){
    field.setAccessible(true);
    try {
      field.set(request, request.getCommandParameters().get(field.getAnnotation(CommandParam.class).pos()));
    } catch (IllegalAccessException e) {
      // won't be illegal as we set it as accessible before
    }
  }
}
