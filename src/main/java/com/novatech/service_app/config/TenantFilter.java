package com.novatech.service_app.config;

import com.novatech.service_app.entity.Tenant;
import com.novatech.service_app.service.TenantContext;
import com.novatech.service_app.service.TenantService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter that extracts subdomain from request and sets tenant context.
 * Runs on every HTTP request BEFORE security filters.
 */
@Component
public class TenantFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    private static final String TENANT_ID_ATTRIBUTE = "TENANT_ID";

    @Autowired
    private TenantService tenantService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String serverName = httpRequest.getServerName();

        logger.debug("🔍 TenantFilter - Processing request for: {}", serverName);

        // Extract subdomain
        String subdomain = extractSubdomain(serverName);

        if (subdomain == null || subdomain.isEmpty()) {
            // No subdomain = Superadmin context
            logger.debug("✅ Superadmin context (no subdomain)");
            TenantContext.clear();
            httpRequest.setAttribute(TENANT_ID_ATTRIBUTE, null);
        } else {
            // Tenant context - lookup tenant
            Optional<Tenant> tenant = tenantService.getTenantBySubdomain(subdomain);

            if (tenant.isPresent()) {
                Long tenantId = tenant.get().getId();
                TenantContext.setTenantId(tenantId);
                httpRequest.setAttribute(TENANT_ID_ATTRIBUTE, tenantId); // ✅ Store in request
                logger.debug("✅ Tenant context set: {} (ID: {})", subdomain, tenantId);
            } else {
                logger.warn("⚠️ Tenant not found for subdomain: {}", subdomain);
                TenantContext.clear();
                httpRequest.setAttribute(TENANT_ID_ATTRIBUTE, null);
            }
        }

        // Continue the filter chain
        chain.doFilter(request, response);
    }

    /**
     * Extract subdomain from server name.
     */
    private String extractSubdomain(String serverName) {
        if (serverName == null || serverName.equals("localhost")) {
            return null;
        }

        String[] parts = serverName.split("\\.");
        if (parts.length > 1) {
            return parts[0];
        }

        return null;
    }
}