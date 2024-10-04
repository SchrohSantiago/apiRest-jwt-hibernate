package com.schroh.springboot.crud.jpa.springboot_crud.security;

import com.schroh.springboot.crud.jpa.springboot_crud.security.filter.JwtAuthenticationFilter;
import com.schroh.springboot.crud.jpa.springboot_crud.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Debemos agregar Spring Security en el POM.xml
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception { // Es el responsable de autenticar usuarios en Spring Security.
        return authenticationConfiguration.getAuthenticationManager(); // Obtiene el AuthenticationManager preconfigurado por Spring, listo para ser utilizado en tu aplicación.
    }

    @Bean  // De esta manera inyectamos el BCrypt
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();// Utiliza el algoritmo BCrypt para codificar constrasenas de manera segura
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests( (authz) -> authz
                .requestMatchers(HttpMethod.GET,"/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/register").permitAll()
                .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager())) // aplicamos el filtro de configuracion con los respectivos mensajes de respuestas
                .csrf(config -> config.disable()) // Es seguridad para vistas del lado del servidor ejemplo Java FX, Timeleaft.
                .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Esto es para que no quede autenticado el usuario en un sesion HTTP si no que debe hacerse uso del TOKEN
                .build();
    } // MOMENTANEAMENTE para cualquier ruta /users se permite autorizacion
}
