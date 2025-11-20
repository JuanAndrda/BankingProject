package com.banking.auth;

import java.util.LinkedList;

/**
 * Admin Class - Extends User
 *
 * Represents an administrator user with full system access.
 * Demonstrates POLYMORPHISM - overrides getPermissions() with admin-level permissions.
 *
 * Admin can:
 * - Create and delete customers
 * - Create and delete accounts
 * - View all customers and accounts
 * - Apply interest to savings accounts
 * - Manage system operations
 *
 * Admin CANNOT:
 * - Perform customer transactions (deposit/withdraw/transfer)
 * - They don't have personal accounts
 */
public class Admin extends User {

    /**
     * Constructor: Creates an admin user.
     * Admins do not need to change password on first login.
     *
     * @param username the admin's username
     * @param password the admin's password
     */
    public Admin(String username, String password) {
        super(username, password, UserRole.ADMIN, false);  // Admins don't need to change password
    }

    /**
     * Returns the LinkedList of permissions for an admin user.
     * Demonstrates POLYMORPHISM - admin has more permissions than customer.
     * Uses LinkedList for consistency with rest of project (CIT 207 & CC 204).
     *
     * @return LinkedList of permission strings this admin has
     */
    @Override
    public LinkedList<String> getPermissions() {
        LinkedList<String> permissions = new LinkedList<>();

        // Session management
        permissions.add("LOGOUT");
        permissions.add("EXIT_APP");

        // Customer operations
        permissions.add("CREATE_CUSTOMER");
        permissions.add("VIEW_CUSTOMER");
        permissions.add("VIEW_ALL_CUSTOMERS");
        permissions.add("DELETE_CUSTOMER");

        // Account operations
        permissions.add("CREATE_ACCOUNT");
        permissions.add("VIEW_ACCOUNT_DETAILS");
        permissions.add("VIEW_ALL_ACCOUNTS");
        permissions.add("DELETE_ACCOUNT");
        permissions.add("UPDATE_OVERDRAFT");

        // Profile operations
        permissions.add("CREATE_PROFILE");
        permissions.add("UPDATE_PROFILE");

        // Transaction operations (viewing/managing)
        permissions.add("VIEW_TRANSACTION_HISTORY");

        // Reporting & utilities
        permissions.add("APPLY_INTEREST");
        permissions.add("SORT_BY_NAME");
        permissions.add("SORT_BY_BALANCE");
        permissions.add("VIEW_AUDIT_TRAIL");

        return permissions;
    }

    @Override
    public String toString() {
        return String.format("Admin[Username=%s]", getUsername());
    }
}
