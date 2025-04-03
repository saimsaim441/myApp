package com.customer.registration.config;




import com.customer.registration.filter.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/auth/signup", "/auth/login", "/h2-console/**", "/api/**", "/auth/**")) // Disable CSRF for these endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/signup", "/auth/login", "/h2-console/**", "/api/**", "/auth/**").permitAll() // Allow public access
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable()) // Allow H2 Console to work
            .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) // Ensure JWT Filter works
            .build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/signup", "/auth/login","/h2-console/**").permitAll()
//                .anyRequest().authenticated()
//            )
//            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
//            .headers(headers -> headers.frameOptions().disable())
//            .build();
//    }
}

