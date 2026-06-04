package com.ats.user.infrastructure.in.web.security;

import com.ats.user.infrastructure.in.web.security.jwt.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("USER_CREATE")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("USER_READ")
                        .requestMatchers(HttpMethod.GET, "/api/users/roles").hasAnyAuthority("USER_READ", "USER_UPDATE")
                        .requestMatchers(HttpMethod.GET, "/api/users/*").hasAuthority("USER_READ")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasAuthority("USER_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasAuthority("USER_DELETE")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/*/status").hasAuthority("USER_STATUS_UPDATE")

                        .requestMatchers(HttpMethod.POST, "/api/roles").hasAuthority("ROLE_CREATE")
                        .requestMatchers(HttpMethod.GET, "/api/roles").hasAuthority("ROLE_READ")
                        .requestMatchers(HttpMethod.GET, "/api/roles/*").hasAuthority("ROLE_READ")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*").hasAuthority("ROLE_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/*").hasAuthority("ROLE_DELETE")
                        .requestMatchers(HttpMethod.PATCH, "/api/roles/*/status").hasAuthority("ROLE_STATUS_UPDATE")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*/permissions").hasAuthority("ROLE_PERMISSION_ASSIGN")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/*/permissions").hasAuthority("ROLE_PERMISSION_REMOVE")

                        .requestMatchers(HttpMethod.POST, "/api/modules").hasAuthority("MODULE_CREATE")
                        .requestMatchers(HttpMethod.GET, "/api/modules").hasAuthority("MODULE_READ")
                        .requestMatchers(HttpMethod.GET, "/api/modules/*").hasAuthority("MODULE_READ")
                        .requestMatchers(HttpMethod.PUT, "/api/modules/*").hasAuthority("MODULE_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/modules/*").hasAuthority("MODULE_DELETE")

                        .requestMatchers(HttpMethod.POST, "/api/menus").hasAuthority("MENU_CREATE")
                        .requestMatchers(HttpMethod.GET, "/api/menus").hasAuthority("MENU_READ")
                        .requestMatchers(HttpMethod.GET, "/api/menus/*").hasAuthority("MENU_READ")
                        .requestMatchers(HttpMethod.PUT, "/api/menus/*").hasAuthority("MENU_UPDATE")
                        .requestMatchers(HttpMethod.DELETE, "/api/menus/*").hasAuthority("MENU_DELETE")

                        .requestMatchers(HttpMethod.GET, "/api/permissions").hasAuthority("PERMISSION_READ")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) ->
                writeError(response, request, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentication required");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                writeError(response, request, HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission");
    }

    private void writeError(HttpServletResponse response,
                            HttpServletRequest request,
                            HttpStatus status,
                            String code,
                            String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("code", code);
        body.put("message", message);
        body.put("path", request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
