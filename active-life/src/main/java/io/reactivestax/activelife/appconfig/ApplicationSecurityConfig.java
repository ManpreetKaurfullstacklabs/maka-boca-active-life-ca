package io.reactivestax.activelife.appconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivestax.activelife.constants.SecurityConstants;
import io.reactivestax.activelife.utility.jwt.JwtAuthenticationFilter;
import io.reactivestax.activelife.utility.jwt.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

@EnableMethodSecurity
public class ApplicationSecurityConfig {

    @Autowired
    private ObjectMapper mapper;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
//        disabling the CSRF
        http.csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(HttpMethod.GET, "/swagger-ui/**","/v3/api-docs/**","/v3/api-docs/swagger-config").permitAll())
                        .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(HttpMethod.POST, SecurityConstants.LOGIN_URL).permitAll())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                                        .addFilter(new JwtAuthenticationFilter(authenticationManager,mapper)) // authenticator filter takes username password in json payload, validate and generate JWT token
                                        .addFilter(new JwtAuthorizationFilter(authenticationManager))
                                     .sessionManagement(sessionManagement ->sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // stateless session management, spring security wouldn't set jsession cookie
        ;
        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
