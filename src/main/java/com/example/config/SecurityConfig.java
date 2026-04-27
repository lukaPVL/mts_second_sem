package com.example.config;

import com.example.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/external/v1/**").permitAll()
                        .requestMatchers("/api/v1/profile").hasRole("USER")
                        .requestMatchers("/api/v1/docs").hasAuthority("READ_PRIVILEGE")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Value("${app.security.pepper}")
    private String pepper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

            @Override
            public String encode(CharSequence rawPassword) {
                return delegate.encode(rawPassword + pepper);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return delegate.matches(rawPassword + pepper, encodedPassword);
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        UserDetails reader = User.withUsername("reader")
                .password(encoder.encode("password"))
                .roles("USER")
                .authorities("READ_PRIVILEGE")
                .build();

        return new InMemoryUserDetailsManager(user, reader);
    }
}
