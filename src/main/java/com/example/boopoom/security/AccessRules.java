package com.example.boopoom.security;

public final class AccessRules {

    private AccessRules() {
    }

    public static final String[] PUBLIC_PATHS = {
            "/",
            "/login",
            "/users/new",
            "/css/**",
            "/js/**",
            "/uploads/**"
    };

    // Admin only: product create/edit, user management, trade search without point usage
    public static final String[] ADMIN_PATHS = {
            "/products/new/**",
            "/products/*/edit",
            "/users",
            "/users/*",
            "/admin/trades",
            "/admin/trades/search",
            "/admin/trades/scatter"
    };

    // User only: trade report and point-based trade search
    public static final String[] USER_PATHS = {
            "/trades/new",
            "/trades",
            "/trades/search",
            "/trades/scatter"
    };

    // Shared: product list/detail for both user and admin
    public static final String[] SHARED_PATHS = {
            "/products",
            "/products/*"
    };
}
