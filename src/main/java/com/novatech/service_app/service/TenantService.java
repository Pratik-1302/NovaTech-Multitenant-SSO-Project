package com.novatech.service_app.service;

import com.novatech.service_app.entity.Tenant;
import com.novatech.service_app.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing tenants.
 */
@Service
public class TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Create a new tenant (called by Superadmin).
     */
    public Tenant createTenant(String name, String email, String password, String subdomain) {
        // Validation
        if (tenantRepository.existsByEmail(email)) {
            throw new RuntimeException("Tenant with email " + email + " already exists");
        }
        if (tenantRepository.existsBySubdomain(subdomain)) {
            throw new RuntimeException("Subdomain " + subdomain + " already taken");
        }

        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setEmail(email);
        // tenant.setPasswordHash(passwordEncoder.encode(password));
        tenant.setSubdomain(subdomain);

        return tenantRepository.save(tenant);
    }

    /**
     * Get all tenants (for Superadmin dashboard).
     */
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    /**
     * Get tenant by ID.
     */
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    /**
     * Get tenant by subdomain (used by TenantFilter).
     */
    public Optional<Tenant> getTenantBySubdomain(String subdomain) {
        return tenantRepository.findBySubdomain(subdomain);
    }

    /**
     * Get tenant by email (used for tenant-admin login).
     */
    public Optional<Tenant> getTenantByEmail(String email) {
        return tenantRepository.findByEmail(email);
    }

    /**
     * Update tenant details.
     */
    public Tenant updateTenant(Long id, String name, String email, String subdomain) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        tenant.setName(name);
        tenant.setEmail(email);
        tenant.setSubdomain(subdomain);

        return tenantRepository.save(tenant);
    }

    /**
     * package com.novatech.service_app.service;
     * 
     * import com.novatech.service_app.entity.Tenant;
     * import com.novatech.service_app.repository.TenantRepository;
     * import com.novatech.service_app.repository.UserRepository; // Added import
     * import com.novatech.service_app.dto.TenantRegistrationRequest; // Added
     * import
     * import com.novatech.service_app.entity.User; // Added import
     * import org.springframework.beans.factory.annotation.Autowired;
     * import org.springframework.security.crypto.password.PasswordEncoder;
     * import org.springframework.stereotype.Service;
     * import org.springframework.transaction.annotation.Transactional; // Added
     * import
     * 
     * import java.util.List;
     * import java.util.Optional;
     * 
     * /**
     * Service for managing tenants.
     */
    @Service
    public class TenantService {

        @Autowired
        private TenantRepository tenantRepository;

        @Autowired
        private UserRepository userRepository; // Added injection

        @Autowired
        private PasswordEncoder passwordEncoder;

        /**
         * Create a new tenant (called by Superadmin).
         */
        public Tenant createTenant(String name, String email, String password, String subdomain) {
            // Validation
            if (tenantRepository.existsByEmail(email)) {
                throw new RuntimeException("Tenant with email " + email + " already exists");
            }
            if (tenantRepository.existsBySubdomain(subdomain)) {
                throw new RuntimeException("Subdomain " + subdomain + " already taken");
            }

            Tenant tenant = new Tenant();
            tenant.setName(name);
            tenant.setEmail(email);
            // tenant.setPasswordHash(passwordEncoder.encode(password));
            tenant.setSubdomain(subdomain);

            return tenantRepository.save(tenant);
        }

        /**
         * Get all tenants (for Superadmin dashboard).
         */
        public List<Tenant> getAllTenants() {
            return tenantRepository.findAll();
        }

        /**
         * Get tenant by ID.
         */
        public Optional<Tenant> getTenantById(Long id) {
            return tenantRepository.findById(id);
        }

        /**
         * Get tenant by subdomain (used by TenantFilter).
         */
        public Optional<Tenant> getTenantBySubdomain(String subdomain) {
            return tenantRepository.findBySubdomain(subdomain);
        }

        /**
         * Get tenant by email (used for tenant-admin login).
         */
        public Optional<Tenant> getTenantByEmail(String email) {
            return tenantRepository.findByEmail(email);
        }

        /**
         * Update tenant details.
         */
        public Tenant updateTenant(Long id, String name, String email, String subdomain) {
            Tenant tenant = tenantRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

            tenant.setName(name);
            tenant.setEmail(email);
            tenant.setSubdomain(subdomain);

            return tenantRepository.save(tenant);
        }

        /**
         * Delete tenant.
         */
        public void deleteTenant(Long id) {
            tenantRepository.deleteById(id);
        }

        /**
         * Register a new tenant and create the admin user.
         */
        @Transactional
        public Tenant registerTenant(TenantRegistrationRequest request) {
            // 1. Validation
            if (tenantRepository.existsBySubdomain(request.getSubdomain())) {
                throw new RuntimeException("Subdomain " + request.getSubdomain() + " is already taken.");
            }

            // Check if email exists globally?
            // For now, we allow same email in different tenants, but for a NEW tenant,
            // we should probably check if this email is already a superadmin or something?
            // Let's just check if it exists in the context of "no tenant" (which shouldn't
            // happen for normal users)
            // or just proceed. The user uniqueness is usually per tenant.

            // 2. Create Tenant
            Tenant tenant = new Tenant();
            tenant.setName(request.getTenantName());
            tenant.setEmail(request.getEmail()); // Tenant contact email
            tenant.setSubdomain(request.getSubdomain());
            tenant = tenantRepository.save(tenant);

            // 3. Create Admin User for this Tenant
            User adminUser = new User();
            adminUser.setFullName(request.getFullName());
            adminUser.setEmail(request.getEmail());
            adminUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            adminUser.setRole("ROLE_ADMIN"); // Tenant Admin
            adminUser.setTenant(tenant);

            userRepository.save(adminUser);

            return tenant;
        }

        /**
         * Validate tenant credentials (for tenant-admin login).
         */
        // public boolean validateTenantCredentials(String email, String password) {
        // Optional<Tenant> tenant = tenantRepository.findByEmail(email);
        // return tenant.isPresent() &&
        //// passwordEncoder.matches(password, tenant.get().getPasswordHash());
        // }
    }
}