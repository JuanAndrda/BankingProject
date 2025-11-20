package com.banking.auth;

import java.util.LinkedList;
import com.banking.utilities.ValidationPatterns;

/**
 * User Abstract Class
 *
 * Base class for all user types (Admin, Customer).
 * Demonstrates INHERITANCE and ABSTRACTION principles (CIT 207).
 *
 * Key features:
 * - ENCAPSULATION: Private username/password
 * - ABSTRACTION: Abstract getPermissions() method
 * - POLYMORPHISM: Each subclass implements permissions differently
 *
 * This class is extended by Admin and Customer classes to provide
 * role-specific behavior and permissions.
 */
public abstract class User {
    private final String username;
    private final String password;
    private final UserRole userRole;
    private boolean passwordChangeRequired;  // Mutable: can change during user lifecycle

    /**
     * Constructor: Creates a user with credentials.
     * Validates and assigns fields.
     * Username and password are final (immutable after creation).
     * passwordChangeRequired is mutable (changes after password change).
     *
     * @param username the user's login username (non-empty)
     * @param password the user's login password (non-empty)
     * @param userRole the user's role (ADMIN or CUSTOMER, non-null)
     * @param passwordChangeRequired whether user must change password on first login
     * @throws IllegalArgumentException if username, password, or userRole is invalid
     */
    public User(String username, String password, UserRole userRole, boolean passwordChangeRequired) {
        this.username = validateUsername(username);
        this.password = validatePassword(password);
        this.userRole = validateUserRole(userRole);
        this.passwordChangeRequired = passwordChangeRequired;
    }

    // ===== VALIDATION METHODS (Private - Used in Constructor) =====

    /**
     * Validates and returns username.
     *
     * @param username the username to validate (non-empty)
     * @return validated username
     * @throws IllegalArgumentException if username is null or empty
     */
    private String validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.USERNAME_EMPTY_ERROR);
        }
        return username;
    }

    /**
     * Validates and returns password.
     *
     * @param password the password to validate (non-empty)
     * @return validated password
     * @throws IllegalArgumentException if password is null or empty
     */
    private String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.PASSWORD_EMPTY_ERROR);
        }
        return password;
    }

    /**
     * Validates and returns user role.
     *
     * @param userRole the user role to validate (non-null)
     * @return validated user role
     * @throws IllegalArgumentException if userRole is null
     */
    private UserRole validateUserRole(UserRole userRole) {
        if (userRole == null) {
            throw new IllegalArgumentException(ValidationPatterns.USER_ROLE_NULL_ERROR);
        }
        return userRole;
    }

    // ===== GETTERS =====

    public String getUsername() { return this.username; }
    public UserRole getUserRole() { return this.userRole; }
    public boolean isPasswordChangeRequired() { return this.passwordChangeRequired; }

    // ===== SETTERS (Mutable Fields Only) =====

    /**
     * Sets whether this user must change their password on next login.
     * Called after user successfully changes password.
     *
     * @param required true if user must change password, false otherwise
     */
    public void setPasswordChangeRequired(boolean required) {
        this.passwordChangeRequired = required;
    }

    /**
     * Abstract method: Subclasses implement role-specific permissions.
     * Demonstrates POLYMORPHISM - each role defines its own permissions.
     *
     * @return LinkedList of permissions this user has
     */
    public abstract LinkedList<String> getPermissions();

    // ===== SECURITY & AUTHENTICATION METHODS =====

    /**
     * Authenticates the user by checking the provided password.
     *
     * @param providedPassword the password to verify
     * @return true if password matches, false otherwise
     */
    public boolean authenticate(String providedPassword) {
        return this.password.equals(providedPassword);
    }

    /**
     * Checks if this user has a specific permission.
     * Iterates through LinkedList to find permission.
     *
     * @param permission the permission to check
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        if (permission == null) return false;
        for (String p : getPermissions()) {
            if (p.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("User[Username=%s, Role=%s]", getUsername(), getUserRole().getDisplayName());
    }
}
