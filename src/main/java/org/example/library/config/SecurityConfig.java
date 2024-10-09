package org.example.library.config;

import lombok.AllArgsConstructor;
import org.example.library.security.JwtAuthenticationFilter;
import org.example.library.security.JwtTokenProvider;
import org.example.library.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for the security of the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    /**
     * The CustomUserDetailsService to use for the security configuration.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * The JwtTokenProvider to use for the security configuration.
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Creates a JwtAuthenticationFilter bean.
     *
     * @return The JwtAuthenticationFilter bean
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(this.jwtTokenProvider, this.userDetailsService);
    }

    /**
     * Creates a SecurityFilterChain bean.
     *
     * @param http The HttpSecurity to use for the SecurityFilterChain
     * @return The SecurityFilterChain bean
     * @throws Exception If an error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/actuator/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(this.jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).build();
    }

    /**
     * Creates an AuthenticationManager bean.
     *
     * @param authenticationConfiguration The AuthenticationConfiguration to use for the AuthenticationManager
     * @return The AuthenticationManager bean
     * @throws Exception If an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates a PasswordEncoder bean.
     *
     * @return The PasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        // Even if we rely only on BCrypt, we use a DelegatingPasswordEncoder to allow for upgrades in the future.
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}