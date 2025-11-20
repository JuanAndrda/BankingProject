package com.banking.models;

import java.util.LinkedList;
import com.banking.utilities.ValidationPatterns;

/**
 * Abstract base class for all banking accounts.
 * Demonstrates ABSTRACTION and INHERITANCE.
 *
 * Concrete subclasses must implement: withdraw()
 *
 * Uses LinkedList to maintain transaction history in chronological order.
 */
public abstract class Account {
    private String accountNo;                       // Format: ACC###
    private double balance;                         // Current account balance
    private Customer owner;                         // Account owner (many-to-1)
    private LinkedList<Transaction> transactionHistory;  // Maintains insertion order

    /**
     * Creates a new account with the given number and owner.
     *
     * @param accountNo account number (format: ACC###)
     * @param owner customer who owns this account
     * @throws IllegalArgumentException if accountNo or owner is invalid
     */
    public Account(String accountNo, Customer owner) {
        this.setAccountNo(accountNo);
        this.setOwner(owner);
        this.balance = 0.0;
        this.transactionHistory = new LinkedList<>();
    }

    /**
     * Deposits money into this account.
     * Validates amount before crediting.
     *
     * @param amount deposit amount (must be positive)
     */
    public void deposit(double amount) {
        if (this.validateAmount(amount)) {
            this.balance += amount;
            System.out.println("✓ Deposited $" + amount + " to " + this.accountNo);
        }
    }

    /**
     * Withdraws money from this account.
     * Subclasses implement specific withdrawal logic (overdraft protection, etc).
     *
     * @param amount withdrawal amount (must be positive)
     * @return true if withdrawal succeeded, false otherwise
     */
    public abstract boolean withdraw(double amount);

    /**
     * Returns formatted account details.
     * Format: ACC### | Owner: Name | Balance: $###.##
     * Uses getters to maintain encapsulation.
     *
     * @return formatted account information
     */
    public String getDetails() {
        String ownerName = (this.owner != null) ? this.owner.getName() : "NO OWNER";
        return String.format("%s | Owner: %s | Balance: $%.2f",
                this.getAccountNo(), ownerName, this.getBalance());
    }

    /**
     * Validates that an amount is positive.
     * Package-private to allow subclasses to use it internally.
     *
     * @param amount amount to validate
     * @return true if amount is positive, false otherwise
     */
    boolean validateAmount(double amount) {
        if (amount <= 0) {
            System.out.println("✗ Invalid amount. Must be positive.");
            return false;
        }
        return true;
    }

    /**
     * Adds a transaction to this account's transaction history.
     * Maintains chronological order using LinkedList.
     *
     * @param t transaction to add
     */
    public void addTransaction(Transaction t) {
        this.transactionHistory.add(t);
    }

    // ===== GETTERS =====
    /** @return account number (format ACC###) */
    public String getAccountNo() { return this.accountNo; }

    /** @return current account balance */
    public double getBalance() { return this.balance; }

    /** @return customer who owns this account */
    public Customer getOwner() { return this.owner; }

    /** @return transaction history (LinkedList maintains insertion order) */
    public LinkedList<Transaction> getTransactionHistory() { return this.transactionHistory; }

    // ===== SETTERS WITH VALIDATION =====
    /**
     * Sets and validates the account number.
     *
     * @param accountNo account number (format: ACC###)
     * @throws IllegalArgumentException if format is invalid
     */
    public void setAccountNo(String accountNo) {
        if (!com.banking.utilities.ValidationPatterns.matchesPattern(accountNo,
                com.banking.utilities.ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(com.banking.utilities.ValidationPatterns.ACCOUNT_NO_ERROR);
        }
        this.accountNo = accountNo;
    }

    /**
     * Sets and validates the account owner.
     *
     * @param owner customer who owns this account (non-null)
     * @throws IllegalArgumentException if owner is null
     */
    public void setOwner(Customer owner) {
        if (owner == null) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_OWNER_NULL_ERROR);
        }
        this.owner = owner;
    }

    /**
     * Sets the account balance (used by subclasses during withdraw/deposit).
     * Protected to restrict access to subclasses only.
     *
     * @param balance new balance amount
     */
    protected void setBalance(double balance) {
        this.balance = balance;
    }
}
