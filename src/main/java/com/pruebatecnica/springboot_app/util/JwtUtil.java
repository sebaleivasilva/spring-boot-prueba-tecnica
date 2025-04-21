package com.pruebatecnica.springboot_app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.KeyGenerator;

@Component
public class JwtUtil {

    private static Key secretKey = null;
                
    // Duración del token (ej: 1 hora)
    private final static long expirationMillis = 3600000;
            
    public JwtUtil() {
        // Generamos la clave secreta usando KeyGenerator
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);  // 256 bits para HMAC-SHA256
            JwtUtil.secretKey = keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generando la clave secreta", e);
        }
    }

    public static UUID validarTokenYObtenerUsuarioId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String usuarioIdStr = claims.getSubject();
            return UUID.fromString(usuarioIdStr);

        } catch (Exception e) {
            throw new RuntimeException("Token inválido o expirado");
        }
    }
    
    public static String generarToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        // Uso de la API moderna para construir el token
        return Jwts.builder()
                .setSubject(userId.toString())  // Usuario como sujeto
                .setIssuedAt(now)  // Fecha de emisión
                .setExpiration(expiryDate)  // Fecha de expiración
                .signWith(secretKey, SignatureAlgorithm.HS256)  // Firmamos con la clave secreta y el algoritmo HS256
                .compact();  // Generamos el token
    }
}