package com.banking.auth;

/**
 * UserRole Enumeration
 *
 * Defines the two user roles in the Banking System:
 * - ADMIN: Full system access (create/delete customers & accounts, manage system)
 * - CUSTOMER: Limited access (deposit, withdraw, transfer from own account only)
 *
 * This demonstrates type-safe role management and satisfies CC 204 requirement
 * for using Enum data structures.
 */
public enum UserRole {
    ADMIN("Administrator"),
    CUSTOMER("Customer");

    private String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
