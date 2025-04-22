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

    private static Key secretKey;
                
    // Duración del token (1 hora)
    private final static long expirationMillis = 3600000;

    public JwtUtil(Key secretKey) {
        JwtUtil.secretKey = secretKey;
    }

    public JwtUtil() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            keyGen.init(256);
            JwtUtil.secretKey = keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generando la clave secreta", e);
        }
    }

    public static UUID validarTokenYObtenerUsuarioId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.secretKey)
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

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(JwtUtil.secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}