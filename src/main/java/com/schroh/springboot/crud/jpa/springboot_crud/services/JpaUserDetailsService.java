package com.schroh.springboot.crud.jpa.springboot_crud.services;

import com.schroh.springboot.crud.jpa.springboot_crud.entities.User;
import com.schroh.springboot.crud.jpa.springboot_crud.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService { //Este servicio implementa la interfaz UserDetailsService de Spring Security. Es el encargado de buscar los detalles del usuario en la base de datos cuando alguien intenta autenticarse. En este caso, se utiliza JPA para obtener los datos de un repositorio.

    // El objetivo es cargar los detalles del usuario desde la base de datos, a partir del nombre de usuario que se recibe como parámetro.
    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> userOptional = repository.findByUsername(username); // busca al usuario en la DB

        if (userOptional.isEmpty()){
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
        } // Si no lo encuentra lanza excepcion

        User user = userOptional.orElseThrow(); // Si lo encuentra lo guarda

        List<GrantedAuthority> authorities = user.getRoles().stream() // Aca realizamos una fuerte INVERSION DE CONTROL por lo cual los roles del usuario se convieerten a una lista de objetos GrantedAuthority
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()); // recoge los roles mapeados en una lista
        
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), // El password es encriptado
                user.isEnable(),
                true,
                true,
                true,
                authorities);
    }// Este servicio es clave en el proceso de autenticación de usuarios. Cuando un usuario intenta iniciar sesión, este servicio busca al usuario en la base de datos por su nombre de usuario, obtiene sus detalles y roles, y los devuelve a Spring Security para su procesamiento. Si el usuario no existe, se lanza una excepción para detener el proceso de autenticación. Los roles son convertidos a un formato que Spring Security puede entender (GrantedAuthority), lo que ayuda a manejar los permisos y accesos dentro de la aplicación.
}
