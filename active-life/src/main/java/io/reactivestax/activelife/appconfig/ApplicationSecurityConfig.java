package io.reactivestax.activelife.appconfig;

import io.reactivestax.activelife.service.MemberRegistrationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApplicationSecurityConfig {

    private final MemberRegistrationService memberRegistrationService;

    public ApplicationSecurityConfig(MemberRegistrationService memberRegistrationService) {
        this.memberRegistrationService = memberRegistrationService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/familyregistration/signup", "/api/familyregistration/login", "/api/familyregistration/login/verify").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/familyregistration/verify/**").permitAll()
                        // Check for GroupOwner directly via SpEL expression
                        .requestMatchers("/api/courseregistration/**")
                        .access("@applicationSecurityConfig.isGroupOwner(authentication)") // Access control based on group ownership
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
