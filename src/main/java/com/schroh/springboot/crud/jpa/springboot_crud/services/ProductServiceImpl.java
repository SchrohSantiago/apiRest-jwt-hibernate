package com.schroh.springboot.crud.jpa.springboot_crud.services;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.Product;
import com.schroh.springboot.crud.jpa.springboot_crud.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true) // Fundamental el uso de transactional por el rolle back
    @Override
    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    @Override
    public Optional<Product> update(Product product, Long id) {
        Optional<Product> productDb = productRepository.findById(id);

        if(productDb.isPresent()){ // Si queremos retornar un Optional usamos IF con isPresent sino utilizamos IFPRESENT con lambda
            Product prod = productDb.orElseThrow();
            prod.setSku(product.getSku());
            prod.setName(product.getName());
            prod.setDescription(product.getDescription());
            prod.setPrice(product.getPrice());

           return Optional.of(productRepository.save(prod));
        }
        return productDb;
    }


    @Transactional
    @Override
    public Optional<Product> delete(Long id) {
        Optional<Product> productDb = productRepository.findById(id);

        productDb.ifPresent(prod -> {
            productRepository.delete(productDb.get());
        });
        return productDb;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }
}
