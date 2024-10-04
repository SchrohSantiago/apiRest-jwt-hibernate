package com.schroh.springboot.crud.jpa.springboot_crud.repositories;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
