package com.schroh.springboot.crud.jpa.springboot_crud.validation;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.Product;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ProductValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Product.class.isAssignableFrom(clazz);
    }

    // La clase ProductValidator basicamente reemplaza a las anotaciones de validacion de Spring, para poder realizar una validacion mas personalizada
    // Usamos anotaciones de Spring validator para validaciones simples y ultizamos la clase validator para validaciones complejas
    @Override
    public void validate(Object target, Errors errors) { // Target es el objeto producto
        Product product = (Product) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"name",null,"es requerido!");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"description","NotEmpty.product.description");
        if (product.getDescription() == null || product.getDescription().isBlank()){
            errors.rejectValue("description",null,"es requerido, por favor!");
        }

        if (product.getPrice() == null){
            errors.rejectValue("price",null, "no puede ser nulo!");
        } else if (product.getPrice() < 500) {
            errors.rejectValue("price",null, "debe ser un valor mayor o igual a 500");
        }
    }
}
