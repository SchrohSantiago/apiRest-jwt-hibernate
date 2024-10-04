package com.schroh.springboot.crud.jpa.springboot_crud.validation;

import com.schroh.springboot.crud.jpa.springboot_crud.services.ProductService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class IsExistsDbValidation implements ConstraintValidator<IsExistsDb, String>{

    @Autowired
    private ProductService service;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (service == null){
            return true;
        }
        return !service.existsBySku(value);
    }
}
