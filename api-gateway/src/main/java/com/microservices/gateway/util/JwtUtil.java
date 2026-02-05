package com.microservices.gateway.util;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilitaire pour la validation des tokens JWT dans la Gateway
 * 
 * @author Baye Rane
 * @version 1.0
 */
@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;

    // Récupère la clé de signature
    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extrait tous les claims d'un token
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.error("Erreur lors de l'extraction des claims: {}", e.getMessage());
            throw e;
        }
    }

    // Extrait un claim spécifique
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrait l'ID utilisateur du token
    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    // Extrait l'email du token
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    // Extrait le rôle du token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extrait le sujet (userId) du token
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // EXtrait la date d'expiration du token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Vérifie si le token est expiré
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'expiration: {}", e.getMessage());
            return true;
        }
    }

    // Valide un token
    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);

            boolean isValid = !isTokenExpired(token);

            if (isValid) {
                log.debug("Token validé avec succès");
            } else {
                log.warn("Token expiré");
            }

            return isValid;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Token JWT invalide: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expiré: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT non supporté: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Token JWT vide: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la validation du token: {}", e.getMessage());
            return false;
        }
    } 
}
