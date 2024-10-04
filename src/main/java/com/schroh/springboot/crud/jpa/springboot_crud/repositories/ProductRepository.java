package com.schroh.springboot.crud.jpa.springboot_crud.repositories;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
    boolean existsBySku(String sku);
}
