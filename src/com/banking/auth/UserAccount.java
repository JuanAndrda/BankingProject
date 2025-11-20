package com.banking.auth;

import java.util.LinkedList;

/**
 * UserAccount Class - Extends User
 *
 * Represents a customer login account with access to all accounts owned by that customer.
 * Demonstrates POLYMORPHISM - overrides getPermissions() with customer-level permissions.
 *
 * UserAccounts can:
 * - Deposit money to any of their own accounts
 * - Withdraw money from any of their own accounts
 * - Transfer money between their own accounts or to other customers
 * - View details for all of their own accounts
 * - View transaction history for all of their own accounts
 *
 * UserAccounts CANNOT:
 * - View other customers' accounts or details
 * - Create or delete accounts
 * - Create or delete customers
 * - Modify account settings (overdraft, interest, etc.)
 *
 * This models real-world banking where customers can manage multiple accounts
 * but only access their own accounts.
 */
public class UserAccount extends User {
    private final String linkedCustomerId;  // The customer ID this user is linked to (immutable)

    /**
     * Constructor: Creates a customer user account linked to a customer.
     * Customers must change their auto-generated password on first login.
     *
     * @param username the customer's username for login
     * @param password the customer's password for login
     * @param linkedCustomerId the customer ID this user represents (format: C###)
     * @throws IllegalArgumentException if linkedCustomerId format is invalid
     */
    public UserAccount(String username, String password, String linkedCustomerId) {
        super(username, password, UserRole.CUSTOMER, true);  // Customers must change password on first login
        this.linkedCustomerId = validateLinkedCustomerId(linkedCustomerId);
    }

    // ===== VALIDATION METHOD (Private - Used in Constructor) =====

    /**
     * Validates and returns the linked customer ID.
     *
     * @param linkedCustomerId the customer ID to validate (format: C###)
     * @return validated customer ID
     * @throws IllegalArgumentException if customer ID format is invalid
     */
    private String validateLinkedCustomerId(String linkedCustomerId) {
        if (!com.banking.utilities.ValidationPatterns.matchesPattern(linkedCustomerId,
                com.banking.utilities.ValidationPatterns.CUSTOMER_ID_PATTERN)) {
            throw new IllegalArgumentException(com.banking.utilities.ValidationPatterns.CUSTOMER_ID_ERROR);
        }
        return linkedCustomerId;
    }

    // ===== CUSTOMER-SPECIFIC OPERATIONS =====

    /**
     * Returns the customer ID linked to this user.
     * This customer owns multiple accounts that this user can access.
     *
     * @return the customer ID
     */
    public String getLinkedCustomerId() {
        return this.linkedCustomerId;
    }

    // ===== PERMISSION OVERRIDE (Polymorphism) =====

    /**
     * Returns the LinkedList of permissions for a customer user account.
     * Demonstrates POLYMORPHISM - customer has limited permissions.
     * Uses LinkedList for consistency with rest of project (CIT 207 & CC 204).
     *
     * @return LinkedList of permission strings this customer account has
     */
    @Override
    public LinkedList<String> getPermissions() {
        LinkedList<String> permissions = new LinkedList<>();

        // Session management
        permissions.add("LOGOUT");
        permissions.add("EXIT_APP");

        // Account operations (own accounts only)
        permissions.add("VIEW_ACCOUNT_DETAILS");

        // Transaction operations (own accounts only)
        permissions.add("DEPOSIT");
        permissions.add("WITHDRAW");
        permissions.add("TRANSFER");
        permissions.add("VIEW_TRANSACTION_HISTORY");

        // Security operations
        permissions.add("CHANGE_PASSWORD");

        return permissions;
    }

    @Override
    public String toString() {
        return String.format("UserAccount[Username=%s, LinkedCustomer=%s]", getUsername(), linkedCustomerId);
    }
}
