package com.schroh.springboot.crud.jpa.springboot_crud.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schroh.springboot.crud.jpa.springboot_crud.security.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;

import static com.schroh.springboot.crud.jpa.springboot_crud.security.TokenJwtConfig.*;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION); // eL TOKEN siempre esta en el header

        if (header == null || !header.startsWith(PREFIX_TOKEN)) { // Si es null o no contieene el prefijo determinado "berear "
            chain.doFilter(request,response);
            return;
        }
        String token = header.replace(PREFIX_TOKEN, ""); // quitamos el prefijo del token y nos quedamos con el token mas limpio

        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload(); // Los claims son para utilizar los datos del token
            String username = claims.getSubject();
            Object authoritiesClaims = claims.get("authorities"); // ROLES

            Collection<? extends GrantedAuthority> authorities = Arrays.asList(
                    new ObjectMapper()
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Almacena el contexto de seguridad de la app
            // Después de ejecutar esta línea, Spring Security ya reconoce al usuario como autenticado y puede usar la información almacenada (nombre de usuario, roles, etc.) para aplicar las reglas de seguridad correspondientes en las próximas solicitudes.
            chain.doFilter(request, response); //  Los filtros en Spring Security están organizados en una secuencia, y este código asegura que, después de configurar la autenticación, el procesamiento continúe con el siguiente filtro.
           /* En resumen:

            El código crea un token de autenticación (UsernamePasswordAuthenticationToken) utilizando los roles y el nombre de usuario obtenidos del token JWT.
            Luego, este token de autenticación se almacena en el SecurityContext para que Spring Security lo reconozca en futuras solicitudes.
                    Finalmente, el procesamiento continúa con el siguiente filtro en la cadena de seguridad. */
        } catch (JwtException e){
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es invalido!");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            response.setContentType(CONTENT_TYPE);
        }

    }
}
