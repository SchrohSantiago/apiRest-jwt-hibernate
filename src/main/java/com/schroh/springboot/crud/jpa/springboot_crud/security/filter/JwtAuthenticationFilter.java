package com.schroh.springboot.crud.jpa.springboot_crud.security.filter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schroh.springboot.crud.jpa.springboot_crud.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;

import static com.schroh.springboot.crud.jpa.springboot_crud.security.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // El propósito principal de este filtro es interceptar las solicitudes de autenticación (como cuando un usuario envía sus credenciales), extraer el nombre de usuario y la contraseña, y delegar el proceso de autenticación al AuthenticationManager.

    private AuthenticationManager authenticationManager; // procesa el token de autenticación y decide si las credenciales proporcionadas son válidas.

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException { // Mediante el request obtenemos el username y el password.. Su tarea es obtener las credenciales del usuario desde la solicitud HTTP, convertirlas en un UsernamePasswordAuthenticationToken, y luego delegar la autenticación al AuthenticationManager.
        User user = null;
        String username = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword(); // Extrae el username y password del objeto User.
        } catch (StreamReadException e){
            e.printStackTrace();
        } catch (DatabindException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password); // Se crea una instancia de UsernamePasswordAuthenticationToken, que es el objeto que Spring Security usa para representar las credenciales de autenticación (en este caso, el nombre de usuario y la contraseña).

        return authenticationManager.authenticate(authenticationToken); // Llama al authenticationManager para autenticar mediante el token
    } // En aplicaciones que usan JWT (JSON Web Token), es común interceptar las solicitudes de autenticación para capturar las credenciales del usuario y generar un token JWT que el cliente usará en las siguientes solicitudes. Aunque en este ejemplo no se maneja directamente el token JWT, este filtro sería el primer paso antes de la creación de dicho token.

    // Este enfoque ofrece flexibilidad para manejar de forma personalizada la forma en que las credenciales son procesadas y autenticadas dentro de la aplicación.


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException { // Filterchain representa la cadena de filtros de seguridad que puede ser continuada. Authentication authResult, contiene el resultado exitoso de la autenticación, incluyendo la información del usuario autenticado.

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)authResult.getPrincipal(); // stamos obteniendo el principal (el usuario autenticado) a partir del objeto authResult. Luego lo convertimos en un objeto de tipo User, que es una implementación de la interfaz UserDetails de Spring Security. De este objeto, sacamos el nombre de usuario (username), que luego será utilizado en la generación del token JWT.
        String username = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities(); // devuelve una colección de objetos que implementan la interfaz GrantedAuthority. Cada instancia representa una autoridad o rol que el usuario posee, por ejemplo: ROLE_ADMIN, ROLE_USER, etc.

        // objeto de tipo Claims para agregar información adicional al JWT (JSON Web Token). Los claims son los datos que vas a incluir dentro del token, y que pueden ser leídos y verificados por el servidor cuando el token sea utilizado en futuras solicitudes.
        Claims claims = Jwts.claims().// Nunca poner datos sensibles
        add("authorities", new ObjectMapper().writeValueAsString(roles)). // clave llamada "authorities" al token con el valor de los roles o autoridades del usuario.
        add("username", username)
                .build();
        // Realizamos esto con los claims para que el token lleve la informacion de los permisos, sin la necesidad de hacer consultas a la base de datos

        String token = Jwts.builder() // Crea un nuevo token JWT
                .subject(username) // Establece el "subject" (o sujeto) del token como el nombre de usuario del usuario autenticado
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // El token expira en 1 hora
                .issuedAt(new Date()) // Establece la fecha y hora en la cual se crea el token
                .signWith(SECRET_KEY) // Firma el token con una clave secreta (SECRET_KEY)
                .compact(); // Genera el token en su forma compacta, es decir, en su formato de cadena final

        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token); // Esto añade una cabecera a la respuesta HTTP llamada Authentication, con el token que acabamos de generar

        Map<String, String> body = new HashMap<>(); // Construimos el cuerpo de la respuesta y luego lo transformamos en json
        body.put("token", token);
        body.put("username", username);
        body.put("message", String.format("Hola %s, iniciaste sesion con exito.", username));

        response.getWriter().write(new ObjectMapper().writeValueAsString(body)); // Escribe el cuerpo de la respuesta como una cadena de texto en formato JSON. Para convertir el mapa body a JSON, usamos ObjectMapper (de la librería Jackson).
        response.setContentType(CONTENT_TYPE); // Indica que el contenido de la respuesta será JSON.
        response.setStatus(200);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException { // En el caso de que la autenticacion falle...
        Map<String, String> body = new HashMap<>();
        body.put("message", "Error en la autenticacion. Username o Password incorrectos"); // Nunca especificar el input que esta mal, porque damos paso a una persona mal intencionada a guiarlo en su error
        body.put("error", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(CONTENT_TYPE);
    }
}
