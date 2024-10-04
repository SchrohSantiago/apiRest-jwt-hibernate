package com.schroh.springboot.crud.jpa.springboot_crud.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredValidation.class)
@Retention(RetentionPolicy.RUNTIME)
// Los objetivos son un ATRIBUTO = FIELD y un METHOD
@Target({ElementType.FIELD, ElementType.METHOD}) // Objetivo, podria ser un field o nombre de campo
public @interface IsRequired {

    // Estos 3 metodos los obtuvimos de la anotacion IsBlank
    String message() default "es requerido usando anotaciones";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
