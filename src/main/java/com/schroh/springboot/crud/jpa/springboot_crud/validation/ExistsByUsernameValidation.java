package com.schroh.springboot.crud.jpa.springboot_crud.validation;


import com.schroh.springboot.crud.jpa.springboot_crud.services.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExistsByUsernameValidation implements ConstraintValidator<ExistsByUsername, String> {

    @Autowired
    private UserService service;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
       if (service == null){
           return true;
       }

        return !service.existsByUsername(username);
    }


}
