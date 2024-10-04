package com.schroh.springboot.crud.jpa.springboot_crud.security;

import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;

public class TokenJwtConfig {

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build(); // Llave ultra robusta que no viaja entre el back a la base de datos

    public static final String PREFIX_TOKEN = "Bearer ";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String CONTENT_TYPE = "application/json";
}
