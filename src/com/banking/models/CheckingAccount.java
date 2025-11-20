package com.banking.models;

import com.banking.utilities.ValidationPatterns;

/**
 * Checking account with overdraft protection.
 * Demonstrates POLYMORPHISM (overrides withdraw() and getDetails()).
 *
 * Features:
 * - Allows overdraft up to overdraftLimit
 * - Can go negative as long as balance + overdraftLimit >= withdrawal amount
 * - Overdraft limit can be configured and updated
 * - Displays overdraft limit in account details
 */
public class CheckingAccount extends Account {
    private double overdraftLimit;  // Maximum overdraft protection amount

    /**
     * Creates a new checking account with overdraft protection.
     *
     * @param accountNo account number (format: ACC###)
     * @param owner customer who owns this account
     * @param overdraftLimit maximum overdraft amount (must be non-negative)
     * @throws IllegalArgumentException if overdraftLimit is negative
     */
    public CheckingAccount(String accountNo, Customer owner, double overdraftLimit) {
        super(accountNo, owner);
        this.setOverdraftLimit(overdraftLimit);
    }

    // ===== POLYMORPHIC OVERRIDES =====

    /**
     * Withdraws money from checking account (with overdraft protection).
     * Allows withdrawal if: (balance + overdraftLimit) >= withdrawal amount
     *
     * @param amount withdrawal amount (must be positive)
     * @return true if withdrawal succeeded, false if exceeds overdraft limit or invalid amount
     */
    @Override
    public boolean withdraw(double amount) {
        if (!this.validateAmount(amount)) return false;

        if (amount > this.getBalance() + this.overdraftLimit) {
            System.out.println("✗ Exceeds overdraft limit. Available: $" +
                    (this.getBalance() + this.overdraftLimit));
            return false;
        }

        this.setBalance(this.getBalance() - amount);
        System.out.println("✓ Withdrew $" + amount + " from " + this.getAccountNo());
        return true;
    }

    /**
     * Returns formatted account details with [CHECKING] tag and overdraft limit.
     * Format: [CHECKING] ACC### | Owner: Name | Balance: $###.## | Overdraft: $###.##
     *
     * @return formatted checking account details
     */
    @Override
    public String getDetails() {
        return "[CHECKING] " + super.getDetails() + " | Overdraft: $" + this.getOverdraftLimit();
    }

    // ===== GETTERS =====

    /**
     * @return maximum overdraft protection amount
     */
    public double getOverdraftLimit() { return this.overdraftLimit; }

    // ===== SETTERS WITH VALIDATION =====

    /**
     * Sets and validates the overdraft limit.
     *
     * @param limit maximum overdraft amount (must be non-negative)
     * @throws IllegalArgumentException if limit is negative
     */
    public void setOverdraftLimit(double limit) {
        if (limit < 0) {
            throw new IllegalArgumentException(ValidationPatterns.OVERDRAFT_NEGATIVE_ERROR);
        }
        this.overdraftLimit = limit;
    }
}
