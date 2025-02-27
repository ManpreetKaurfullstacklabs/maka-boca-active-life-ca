package io.reactivestax.activelife.utility.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.reactivestax.activelife.Enums.Role;
import org.springframework.stereotype.Component;

import java.util.Date;
import io.reactivestax.activelife.constants.SecurityConstants;



@Component
public class JwtUtil {

    private long expirationTime = 3600000;
    //private static final String SECRET_KEY = "your-secret-key";

    public String generateToken(String memberLoginId, String pin) {
        return JWT.create()
                .withSubject(memberLoginId)
                .withClaim("pin", pin)
                .withClaim("role", Role.USER.name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET));
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(SecurityConstants.SECRET))
                    .build()
                    .verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
