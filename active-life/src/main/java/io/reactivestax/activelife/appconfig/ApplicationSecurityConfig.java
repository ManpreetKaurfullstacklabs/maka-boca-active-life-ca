package io.reactivestax.activelife.appconfig;

import io.reactivestax.activelife.service.AppUserDetailsServiceImpl;
import io.reactivestax.activelife.utility.jwt.JwtAuthenticationFilter;
import io.reactivestax.activelife.utility.jwt.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig {

    private final UserDetailsService userDetailsService;
   // private  final AuthenticationManager authenticationManager;

    public ApplicationSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
       // this.authenticationManager = authenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/familyregistration/signup",
                                "/api/familyregistration/login", "/api/familyregistration/login/verify" )
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/familyregistration/verify/**")
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )
               // .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .addFilter(new JwtAuthenticationFilter(authenticationManager))
                .addFilter(new JwtAuthorizationFilter(authenticationManager))
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
