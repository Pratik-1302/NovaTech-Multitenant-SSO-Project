package com.novatech.service_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Security Headers Configuration for Production
 * Adds essential security headers to protect against common web vulnerabilities
 */
@Configuration
@Profile("prod")
public class SecurityHeadersConfig {

    @Value("${app.allowed-origins:https://pratiktech.cloud,https://www.pratiktech.cloud}")
    private String allowedOrigins;

    /**
     * Add security headers to all HTTP responses
     * - HSTS: Force HTTPS
     * - X-Frame-Options: Prevent clickjacking
     * - X-Content-Type-Options: Prevent MIME sniffing
     * - X-XSS-Protection: Enable XSS filter
     * - Referrer-Policy: Control referrer information
     * - Permissions-Policy: Control browser features
     */
    @Bean
    public Filter securityHeadersFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                HttpServletResponse httpResponse = (HttpServletResponse) response;

                // Force HTTPS (HTTP Strict Transport Security)
                // max-age=31536000 (1 year), includeSubDomains, preload
                httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");

                // Prevent clickjacking attacks
                httpResponse.setHeader("X-Frame-Options", "DENY");

                // Prevent MIME type sniffing
                httpResponse.setHeader("X-Content-Type-Options", "nosniff");

                // Enable XSS protection
                httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

                // Control referrer information
                httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

                // Content Security Policy (CSP)
                // Allow resources only from same origin, inline styles for Thymeleaf/Tailwind
                httpResponse.setHeader("Content-Security-Policy",
                        "default-src 'self'; " +
                                "script-src 'self' 'unsafe-inline'; " +
                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                                "img-src 'self' data: https:; " +
                                "font-src 'self' https://cdn.jsdelivr.net; " +
                                "frame-ancestors 'none'; " +
                                "base-uri 'self'; " +
                                "form-action 'self'");

                // Permissions Policy (formerly Feature Policy)
                httpResponse.setHeader("Permissions-Policy",
                        "geolocation=(), microphone=(), camera=(), payment=()");

                // Server header removal (don't advertise server technology)
                httpResponse.setHeader("Server", "NovaTech");

                chain.doFilter(request, response);
            }
        };
    }

    /**
     * CORS Configuration for API access from allowed domains
     * Only needed if you expose REST APIs for external consumption
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse allowed origins from property
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);

        // Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies)
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    /**
     * Request logging filter for production debugging
     * Logs incoming requests with minimal overhead
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(false); // Don't log request body (may contain sensitive data)
        loggingFilter.setMaxPayloadLength(0);
        loggingFilter.setIncludeHeaders(false); // Don't log headers (may contain auth tokens)
        loggingFilter.setAfterMessagePrefix("REQUEST: ");
        return loggingFilter;
    }
}
