package com.schroh.springboot.crud.jpa.springboot_crud.entities;

import com.schroh.springboot.crud.jpa.springboot_crud.validation.IsExistsDb;
import com.schroh.springboot.crud.jpa.springboot_crud.validation.IsRequired;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @IsRequired
    @IsExistsDb
    private String sku;

//    @NotEmpty  NOTNULL ES PARA NUMEROS
    @IsRequired(message = "{IsRequired.product.name}")  // esta asisgnacion proviene del messages.properties es para personalizar los mensajes de error
    @Size(min = 3, max = 25) // Validamos el minimo y maximo de caracteres
    private String name;

    @Min(value = 500, message = "{Min.product.price}")
    @NotNull(message = "{NotNull.product.price)")
    private Integer price;

    @IsRequired// Not blank valida que no sea vacio pero tampoco que ingresen un espacio en blanco
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
