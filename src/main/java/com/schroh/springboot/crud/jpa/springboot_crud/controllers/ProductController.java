package com.schroh.springboot.crud.jpa.springboot_crud.controllers;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.Product;
import com.schroh.springboot.crud.jpa.springboot_crud.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products") // Nunca terminar con /
public class ProductController {

    @Autowired // Inyeccion de la INTERFAZ productService
    private ProductService productService;

    // Inyeccion de clase validator
//    @Autowired
//    private ProductValidator validation;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')") // Tenemos preAuthorize que es basicamente antes de que se ejecute el endpoint, y post authorize despues del endpoint.
    public List<Product> list() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> view(@PathVariable Long id) {
        Optional<Product> productOptional = productService.findById(id);
        if(productOptional.isPresent()){
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody Product product, BindingResult result){ // BINDINGRESULT obtiene todos los errores
//        validation.validate(product,result);
        if (result.hasFieldErrors()){
            return validation(result);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id,@Valid @RequestBody Product product, BindingResult result) {
//        validation.validate(product,result);
        if (result.hasFieldErrors()){
            return validation(result);
        }
        Optional<Product> productOptional = productService.update(product, id);
        if (productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).body(productOptional.orElseThrow());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Product> productOptional = productService.delete(id);
        if(productOptional.isPresent()){
            return ResponseEntity.ok(productOptional.orElseThrow());
        }
        return  ResponseEntity.notFound().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();// La Key es el nombre del atributo que vaildamos y el Value es el mensaje de error
        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
