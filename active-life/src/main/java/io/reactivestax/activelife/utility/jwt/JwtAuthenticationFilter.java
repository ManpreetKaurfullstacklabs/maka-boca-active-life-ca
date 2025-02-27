package io.reactivestax.activelife.utility.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.activelife.constants.SecurityConstants;
import io.reactivestax.activelife.dto.LoginDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    // Constructor to inject AuthenticationManager
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // Called when user attempts to log in
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        try {
            // Parse the login credentials from the request body
            LoginDTO loginDTO = new ObjectMapper().readValue(req.getInputStream(), LoginDTO.class);

            // Create an authentication token with the provided username and password
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getMemberLoginId(), loginDTO.getPin());

            // Authenticate the user using the AuthenticationManager
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            // If there's an error reading the authentication request
            throw new RuntimeException("Error reading the authentication request", e);
        }
    }

    // Called after successful authentication
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        // Create a JWT token with the user's details
        String token = JWT.create()
                .withSubject(auth.getName()) // User's username
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)) // Token expiration
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes())); // Sign the token with your secret key

        // Set the token in the response header
        res.setHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);
    }
}
