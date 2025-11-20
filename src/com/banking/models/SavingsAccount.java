package com.banking.models;

import com.banking.utilities.ValidationPatterns;

/**
 * Savings account that prevents overdrafts and supports interest accrual.
 * Demonstrates POLYMORPHISM (overrides withdraw() and getDetails()).
 *
 * Features:
 * - No overdraft allowed (balance cannot go negative)
 * - Interest rate applied via applyInterest()
 * - Displays interest rate in account details
 */
public class SavingsAccount extends Account {
    private double interestRate;  // Annual interest rate (0.0 to 1.0)

    /**
     * Creates a new savings account with interest rate.
     *
     * @param accountNo account number (format: ACC###)
     * @param owner customer who owns this account
     * @param interestRate annual interest rate (0.0 to 1.0, e.g., 0.03 for 3%)
     * @throws IllegalArgumentException if interestRate is out of bounds
     */
    public SavingsAccount(String accountNo, Customer owner, double interestRate) {
        super(accountNo, owner);
        this.setInterestRate(interestRate);
    }

    // ===== SAVINGS ACCOUNT FEATURE =====

    /**
     * Applies annual interest to this account.
     * Calculates interest as: balance * interestRate
     * Adds interest to current balance.
     */
    public void applyInterest() {
        double interest = this.getBalance() * this.interestRate;
        this.setBalance(this.getBalance() + interest);
        System.out.println("✓ Interest applied: $" + String.format("%.2f", interest));
    }

    // ===== POLYMORPHIC OVERRIDES =====

    /**
     * Withdraws money from savings account (no overdraft allowed).
     * Ensures withdrawal does not exceed current balance.
     *
     * @param amount withdrawal amount (must be positive)
     * @return true if withdrawal succeeded, false if insufficient funds or invalid amount
     */
    @Override
    public boolean withdraw(double amount) {
        if (!this.validateAmount(amount)) return false;

        if (amount > this.getBalance()) {
            System.out.println("✗ Insufficient funds. Available: $" + this.getBalance());
            return false;
        }

        this.setBalance(this.getBalance() - amount);
        System.out.println("✓ Withdrew $" + amount + " from " + this.getAccountNo());
        return true;
    }

    /**
     * Returns formatted account details with [SAVINGS] tag and interest rate.
     * Format: [SAVINGS] ACC### | Owner: Name | Balance: $###.## | Interest: #%
     *
     * @return formatted savings account details
     */
    @Override
    public String getDetails() {
        return "[SAVINGS] " + super.getDetails() + " | Interest: " + (this.getInterestRate() * 100) + "%";
    }

    // ===== GETTERS =====

    /**
     * @return annual interest rate (0.0 to 1.0)
     */
    public double getInterestRate() { return this.interestRate; }

    // ===== SETTERS WITH VALIDATION =====

    /**
     * Sets and validates the interest rate.
     *
     * @param rate annual interest rate (must be 0.0 to 1.0)
     * @throws IllegalArgumentException if rate is out of bounds
     */
    public void setInterestRate(double rate) {
        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException(ValidationPatterns.INTEREST_RATE_RANGE_ERROR);
        }
        this.interestRate = rate;
    }
}
