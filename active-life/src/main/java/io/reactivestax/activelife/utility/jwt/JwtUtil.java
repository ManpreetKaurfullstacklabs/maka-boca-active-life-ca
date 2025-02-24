package io.reactivestax.activelife.utility.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private long expirationTime = 3600000;
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private Key generateSecretKey(String memberLoginId, String pin) {
        String secretSource = memberLoginId + ":" + pin;
        return Keys.hmacShaKeyFor(secretSource.getBytes());
    }

    public String generateToken(String memberLoginId, String pin) {
        return Jwts.builder()
                .claim("memberLoginId", memberLoginId)
                .claim("pin", pin)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            String memberLoginId = extractMemberLoginId(token);
            String pin = extractPin(token);
            Key secretKey = generateSecretKey(memberLoginId, pin);

            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Invalid token
        }
    }

    public String extractMemberLoginId(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("memberLoginId", String.class);
    }

    public String extractPin(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("pin", String.class);
    }
}
