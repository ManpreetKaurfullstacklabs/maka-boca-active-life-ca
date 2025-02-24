package io.reactivestax.activelife.utility.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.activelife.constants.SecurityConstants;
import io.reactivestax.activelife.dto.LoginDTO;
import io.reactivestax.activelife.service.MemberRegistrationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper mapper;
    private final MemberRegistrationService memberRegistrationService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper, MemberRegistrationService memberRegistrationService) {
        this.authenticationManager = authenticationManager;
        this.mapper = mapper;
        this.memberRegistrationService = memberRegistrationService;
        setFilterProcessesUrl(SecurityConstants.LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) {
        LoginDTO loginDTO = null;
        try {
            loginDTO = mapper.readValue(req.getInputStream(), LoginDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug("Attempting to authenticate user: {}", loginDTO);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getMemberLoginId(), loginDTO.getPin(), new ArrayList<>());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        String username = auth.getName();

        boolean isGroupOwner = memberRegistrationService.isGroupOwner(username);

        if (isGroupOwner) {

            String token = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                    .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().write("{\"token\":\"" + token + "\"}");
            res.getWriter().flush();
        } else {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not a Group Owner");
        }
    }
}
