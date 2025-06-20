package com.example.app.config;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_RESOURCES = {
            "/landing",
            "/login**",
            "/images/**",
            "/styles/**",
            "/VAADIN/**",
            "/favicon.ico",
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",
            "/icons/**",
            "/frontend/**",
            "/frontend-es5/**",
            "/frontend-es6/**",
            "/webjars/**",
            "/h2-console/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(this::isFrameworkInternalRequest).permitAll()
                        .requestMatchers(PUBLIC_RESOURCES).permitAll()
                        .requestMatchers("/ws-comments","/comment","/topic/comments","ws://localhost:8080/ws-comments").permitAll()
                        .requestMatchers("/ws-comments/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home", true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("https://dev-m0ghxvh6uxhrx8fp.us.auth0.com/v2/logout?client_id=H3qigV3T2C6SmqQ0lNaAmQbuTTZeoN6p&returnTo=http://localhost:8080/landing")
                );

        return http.build();
    }

    private boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null &&
                Stream.of(HandlerHelper.RequestType.values())
                        .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }
}
