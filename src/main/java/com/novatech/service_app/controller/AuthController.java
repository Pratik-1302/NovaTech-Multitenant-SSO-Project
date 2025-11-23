package com.novatech.service_app.controller;

import com.novatech.service_app.dto.SignupRequest;
import com.novatech.service_app.service.SsoManagementService;
import com.novatech.service_app.service.TenantContext;
import com.novatech.service_app.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SsoManagementService ssoManagementService;

    // ===================== LOGIN PAGE =====================
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            logger.info("User already authenticated, redirecting to /home");
            return "redirect:/home";
        }

        logger.info("=== LOGIN PAGE ACCESSED ===");
        logger.info("Tenant Context: {}", TenantContext.getTenantId());
        logger.info("Error param: {}, Success param: {}", error, success);

        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }

        if (success != null) {
            model.addAttribute("success", "Registration successful! Please login.");
        }

        // ✅ ALWAYS set SSO attributes (with safe defaults)
        Long tenantId = TenantContext.getTenantId();

        try {
            if (tenantId != null) {
                // Tenant context - check SSO settings
                boolean jwtEnabled = ssoManagementService.isJwtEnabled();
                boolean oidcEnabled = ssoManagementService.isOidcEnabled();
                boolean samlEnabled = ssoManagementService.isSamlEnabled();

                model.addAttribute("jwtEnabled", jwtEnabled);
                model.addAttribute("oidcEnabled", oidcEnabled);
                model.addAttribute("samlEnabled", samlEnabled);
                model.addAttribute("ssoEnabled", jwtEnabled || oidcEnabled || samlEnabled);

                logger.info("SSO Status - JWT: {}, OIDC: {}, SAML: {}", jwtEnabled, oidcEnabled, samlEnabled);
            } else {
                // Superadmin login - no SSO
                model.addAttribute("jwtEnabled", false);
                model.addAttribute("oidcEnabled", false);
                model.addAttribute("samlEnabled", false);
                model.addAttribute("ssoEnabled", false);
                logger.info("Superadmin login page - SSO disabled");
            }
        } catch (Exception e) {
            // Fallback if SSO check fails
            logger.error("Error checking SSO status: {}", e.getMessage(), e);
            model.addAttribute("jwtEnabled", false);
            model.addAttribute("oidcEnabled", false);
            model.addAttribute("samlEnabled", false);
            model.addAttribute("ssoEnabled", false);
        }

        return "login";
    }

    // ===================== SIGNUP PAGE =====================
    @GetMapping("/signup")
    public String signupPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            logger.info("Authenticated user tried to access signup — redirecting to /home");
            return "redirect:/home";
        }

        Long tenantId = TenantContext.getTenantId();
        logger.info("=== SIGNUP PAGE ACCESSED === (Tenant: {})", tenantId);

        model.addAttribute("signupRequest", new SignupRequest());
        model.addAttribute("tenantContext", tenantId != null);

        return "signup";
    }

    // ===================== SIGNUP FORM HANDLER =====================
    @PostMapping("/signup")
    public String registerUser(
            @Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
            BindingResult result,
            Model model) {

        Long tenantId = TenantContext.getTenantId();
        logger.info("=== SIGNUP FORM SUBMITTED ===");
        logger.info("Full Name: {}, Email: {}, Tenant: {}",
                signupRequest.getFullName(), signupRequest.getEmail(), tenantId);

        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            return "signup";
        }

        if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
            logger.warn("Passwords do not match for email: {}", signupRequest.getEmail());
            model.addAttribute("error", "Passwords do not match");
            return "signup";
        }

        if (userService.emailExists(signupRequest.getEmail())) {
            logger.warn("Email already registered: {}", signupRequest.getEmail());
            model.addAttribute("error", "Email already registered");
            return "signup";
        }

        try {
            userService.registerUser(
                    signupRequest.getFullName(),
                    signupRequest.getEmail(),
                    signupRequest.getPassword()
            );

            if (tenantId != null) {
                logger.info("✅ User registered under tenant: {}", tenantId);
            } else {
                logger.info("✅ User registered (no tenant - superadmin context)");
            }

            return "redirect:/login?success=true";

        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "signup";
        }
    }
}