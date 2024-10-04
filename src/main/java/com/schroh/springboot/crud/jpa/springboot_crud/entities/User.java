package com.schroh.springboot.crud.jpa.springboot_crud.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schroh.springboot.crud.jpa.springboot_crud.validation.ExistsByUsername;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ExistsByUsername
    @Column(unique = true)
    @NotBlank
    @Size(min = 4, max = 80)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Usamos DTO o utilizamos dicha anotacion de Spring para que no devuelva el password cuando se haga un get
    private String password;

    // Lo utilizamos cuando tenemos relaciones bidireccionales y hay listas en los dos lados
    @JsonIgnoreProperties({"users","handler","hibernateLazyInitializer"}) // PARA EVITAR JSON EN BUCLE
    @ManyToMany // no hay cascada ya que el rol no se crea junto al usuario.
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"), // Mapeo de foregin key principal
            inverseJoinColumns = @JoinColumn(name = "role_id"), // Mapeo de foregin key inversa
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id","role_id"})}
    )
    private List<Role> roles;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Transient // Indicamos que NO es una columna de la tabla
    private boolean admin; // si es admin o no,es unicamente una "bandera", no necesitamos el field en la tabla

    private boolean enable;

    public User() {
        roles = new ArrayList<>();
    }

    @PrePersist // Configura el valor por defecto de algun atributo pre persistencia
    public void prePersist() {
        enable = true;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enabled) {
        this.enable = enabled;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
