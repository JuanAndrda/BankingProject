package com.banking.models;

import java.util.LinkedList;
import java.util.Iterator;
import com.banking.utilities.ValidationPatterns;

/**
 * Represents a bank customer with accounts and profile information.
 * Demonstrates 1-to-Many relationship (Customer → Accounts)
 * and 1-to-1 relationship (Customer ↔ CustomerProfile).
 */
public class Customer {
    private String customerId;        // Format: C###
    private String name;              // Customer's full name
    private LinkedList<Account> accounts;      // 1-to-Many: Customer has multiple accounts
    private CustomerProfile profile;  // 1-to-1: Customer has exactly one profile

    /**
     * Creates a new customer with the given ID and name.
     *
     * @param customerId unique customer ID (format: C###)
     * @param name customer's full name (non-empty)
     * @throws IllegalArgumentException if ID or name is invalid
     */
    public Customer(String customerId, String name) {
        this.setCustomerId(customerId);
        this.setName(name);
        this.accounts = new LinkedList<>();
    }

    /**
     * Adds an account to this customer's account list.
     * Demonstrates account management in 1-to-Many relationship.
     * Prevents duplicate accounts from being added.
     *
     * @param a the account to add (must not be null)
     */
    public void addAccount(Account a) {
        if (a != null) {
            // Check if account already exists in customer's account list
            for (Account existing : this.accounts) {
                if (existing.getAccountNo().equals(a.getAccountNo())) {
                    System.out.println("✗ Account " + a.getAccountNo() + " already added to customer " + this.name);
                    return;
                }
            }
            this.accounts.add(a);
            System.out.println("✓ Account " + a.getAccountNo() + " added to customer " + this.name);
        }
    }

    /**
     * Removes an account from this customer's account list.
     * Uses Iterator to safely remove during iteration (prevents ConcurrentModificationException).
     *
     * @param accountNo the account number to remove
     * @return true if account was removed, false if not found
     */
    public boolean removeAccount(String accountNo) {
        Iterator<Account> iterator = this.accounts.iterator();
        while (iterator.hasNext()) {
            Account acc = iterator.next();
            if (acc.getAccountNo().equals(accountNo)) {
                iterator.remove();  // Safe removal during iteration
                System.out.println("✓ Account " + accountNo + " removed from customer " + this.name);
                return true;
            }
        }
        System.out.println("✗ Account " + accountNo + " not found");
        return false;
    }

    // ===== GETTERS =====
    /** @return list of accounts for this customer */
    public LinkedList<Account> getAccounts() { return this.accounts; }

    /** @return customer ID (format C###) */
    public String getCustomerId() { return this.customerId; }

    /** @return customer's name */
    public String getName() { return this.name; }

    /** @return customer profile (1-to-1 relationship) */
    public CustomerProfile getProfile() { return this.profile; }

    // ===== SETTERS WITH VALIDATION =====
    /**
     * Sets and validates the customer ID.
     *
     * @param customerId must be format C### (e.g., C001)
     * @throws IllegalArgumentException if format is invalid
     */
    public void setCustomerId(String customerId) {
        if (!com.banking.utilities.ValidationPatterns.matchesPattern(customerId,
                com.banking.utilities.ValidationPatterns.CUSTOMER_ID_PATTERN)) {
            throw new IllegalArgumentException(com.banking.utilities.ValidationPatterns.CUSTOMER_ID_ERROR);
        }
        this.customerId = customerId;
    }

    /**
     * Sets and validates the customer name.
     *
     * @param name customer's full name (non-empty)
     * @throws IllegalArgumentException if name is empty or null
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.CUSTOMER_NAME_EMPTY_ERROR);
        }
        this.name = name.trim();
    }

    /**
     * Sets the customer profile (1-to-1 relationship).
     * Also establishes bidirectional relationship by setting profile's customer reference.
     *
     * @param profile the customer profile to assign
     */
    public void setProfile(CustomerProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setCustomer(this);
        }
    }

    @Override
    public String toString() {
        return String.format("Customer[ID=%s, Name=%s, Accounts=%d]",
                this.getCustomerId(), this.getName(), this.getAccounts().size());
    }
}
