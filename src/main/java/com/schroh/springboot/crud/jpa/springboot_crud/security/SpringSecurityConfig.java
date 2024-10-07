package com.schroh.springboot.crud.jpa.springboot_crud.security;

import com.schroh.springboot.crud.jpa.springboot_crud.security.filter.JwtAuthenticationFilter;
import com.schroh.springboot.crud.jpa.springboot_crud.security.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration // Debemos agregar Spring Security en el POM.xml
@EnableMethodSecurity(prePostEnabled = true)  // CONFIGURAMOS LAS ANOTACIONES PARA LAS RUTAS Y ROLES
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
                          // Esta es una manera de asignacion de rutas a roles, pero Spring nos provee antoaciones que directamente se incluyen sobre el endpoint para manejar las rutas y los roles
//                        .requestMatchers(HttpMethod.POST,"/api/users").hasRole("ADMIN") // Solo los users con rol ADMIN pueden crear otros usuarios
//                        .requestMatchers(HttpMethod.GET,"/api/products", "/api/products/{id}").hasAnyRole("ADMIN","USER")
//                        .requestMatchers(HttpMethod.POST,"/api/products").hasRole("ADMIN") // solo los admins pueden crear, eliminar o modificar productos
//                        .requestMatchers(HttpMethod.DELETE,"/api/products").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT,"/api/products/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager())) // aplicamos el filtro de configuracion con los respectivos mensajes de respuestas
                .csrf(config -> config.disable()) // Es seguridad para vistas del lado del servidor ejemplo Java FX, Timeleaft.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(managment -> managment.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Esto es para que no quede autenticado el usuario en un sesion HTTP si no que debe hacerse uso del TOKEN
                .build();
    } // MOMENTANEAMENTE para cualquier ruta /users se permite autorizacion

    // Configuracion de CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Se aplicara la configuracion en toda nuestra aplicacion
        return source;
    }

    @Bean // Realizamos un filter para unir con la configuracino...
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // mas ALTA
        return corsBean;
    }
}
