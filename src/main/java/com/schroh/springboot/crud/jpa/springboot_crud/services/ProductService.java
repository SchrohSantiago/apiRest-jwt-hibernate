package com.schroh.springboot.crud.jpa.springboot_crud.services;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService { // En la interfaz de servicio vamos a tener los metodos que la clase service deba implementar

    List<Product> findAll();

    Optional<Product> findById(Long id);

    Product save(Product product);

    Optional<Product> update(Product product, Long id);

    Optional<Product> delete(Long id);

    boolean existsBySku(String sku);
}
