package com.banking.auth;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.banking.utilities.ValidationPatterns;

/**
 * AuditLog Class
 *
 * Records all system operations for security and compliance purposes.
 * Each log entry contains: timestamp, username, action performed, and details.
 *
 * This demonstrates:
 * - Real-world banking security practices (SOX, PCI compliance)
 * - 1-to-many relationship (User has many AuditLogs)
 * - LinkedList usage for chronological logging
 *
 * Example: "2025-01-15 14:30:45 | alice (ADMIN) | CREATED_CUSTOMER | Customer ID: C001"
 */
public class AuditLog {
    private final LocalDateTime timestamp;
    private final String username;
    private final UserRole userRole;
    private final String action;
    private final String details;

    /**
     * Constructor: Creates an audit log entry with validation.
     *
     * @param username the user who performed the action (non-empty)
     * @param userRole the role of the user (ADMIN or CUSTOMER, non-null)
     * @param action the action performed (non-empty, e.g., "CREATED_CUSTOMER", "DEPOSIT")
     * @param details additional details about the action (non-null)
     * @throws IllegalArgumentException if any parameter fails validation
     */
    public AuditLog(String username, UserRole userRole, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.username = validateUsername(username);
        this.userRole = validateUserRole(userRole);
        this.action = validateAction(action);
        this.details = validateDetails(details);
    }

    // ===== VALIDATION SETTERS (Using validation logic in constructor) =====

    /**
     * Validates and returns username.
     *
     * @param username the username to validate (non-empty)
     * @return validated username (trimmed)
     * @throws IllegalArgumentException if username is null or empty
     */
    private String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.USERNAME_EMPTY_ERROR);
        }
        return username.trim();
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

    /**
     * Validates and returns action.
     *
     * @param action the action to validate (non-empty)
     * @return validated action (trimmed)
     * @throws IllegalArgumentException if action is null or empty
     */
    private String validateAction(String action) {
        if (action == null || action.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.ACTION_EMPTY_ERROR);
        }
        return action.trim();
    }

    /**
     * Validates and returns details.
     *
     * @param details the details to validate (non-null)
     * @return validated details (trimmed, can be empty string)
     * @throws IllegalArgumentException if details is null
     */
    private String validateDetails(String details) {
        if (details == null) {
            throw new IllegalArgumentException(ValidationPatterns.DETAILS_NULL_ERROR);
        }
        return details.trim();
    }

    // ===== GETTERS =====

    public LocalDateTime getTimestamp() { return this.timestamp; }
    public String getUsername() { return this.username; }
    public UserRole getUserRole() { return this.userRole; }
    public String getAction() { return this.action; }
    public String getDetails() { return this.details; }

    /**
     * Returns formatted audit log entry.
     * Format: "YYYY-MM-DD HH:MM:SS | username (ROLE) | ACTION | details"
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %s (%s) | %s | %s",
                getTimestamp().format(formatter),
                getUsername(),
                getUserRole().getDisplayName(),
                getAction(),
                getDetails());
    }
}
