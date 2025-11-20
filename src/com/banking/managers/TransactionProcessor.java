package com.banking.managers;

import com.banking.models.*;
import com.banking.utilities.*;
import com.banking.auth.User;
import com.banking.auth.UserRole;
import com.banking.auth.UserAccount;
import com.banking.BankingSystem;
import java.util.*;

/**
 * Transaction Processing Service Class
 *
 * This class handles all transaction-related operations including:
 * - Processing transactions immediately (Deposit, Withdraw, Transfer)
 * - Instant account updates without queuing
 * - Viewing transaction history with Stack-based display (most recent first)
 * - User interface handlers for transaction operations
 *
 * This demonstrates COMPOSITION: BankingSystem HAS-A TransactionProcessor
 * instead of BankingSystem extending TransactionProcessor.
 *
 * Data Structures Used:
 * - Stack<Transaction>: LIFO display of transaction history (CC 204 requirement)
 * - LinkedList<Account>: For finding accounts
 * - LinkedList<Transaction>: Transaction history per account (maintains order)
 *
 * Benefits:
 * - All transaction logic is isolated in one place
 * - Instant processing (no queue delays)
 * - Clear separation from customer, account, and input logic
 * - Transaction counter centralized
 * - Better user experience (most recent transactions shown first)
 */
public class TransactionProcessor {
    private LinkedList<Account> accountList;
    private int txCounter;
    private InputValidator validator;
    private BankingSystem bankingSystem;  // Reference for permission checks and audit logging

    /**
     * Constructor: Initialize processor with references to shared collections.
     *
     * @param accountList reference to LinkedList of all accounts
     * @param validator InputValidator instance for user input
     */
    public TransactionProcessor(LinkedList<Account> accountList, InputValidator validator) {
        this.accountList = accountList;
        this.txCounter = 1;
        this.validator = validator;
        this.bankingSystem = null;  // Set later via setBankingSystem()
    }

    // ===== SECURITY HELPER METHODS =====

    /**
     * Checks if the current user can access a specific account.
     * - Admins can access any account
     * - Customers can only access their own linked account
     *
     * @param accountNo the account number to check access for
     * @return true if user can access the account, false otherwise
     */
    // ===== TRANSACTION OPERATIONS =====
    // Note: canAccessAccount() check is delegated to BankingSystem.canAccessAccount()
    // This ensures a single source of truth for authorization logic across the entire system.

    /**
     * Processes a deposit transaction immediately.
     * Funds are deposited to the account instantly.
     *
     * @param accountNo destination account number
     * @param amount deposit amount (must be positive)
     * @return true if deposit successful, false if account not found
     */
    public boolean deposit(String accountNo, double amount) {
        Account account = AccountUtils.findAccount(this.accountList, accountNo);
        if (account == null) {
            System.out.println("✗ Account not found");
            return false;
        }

        try {
            // Create transaction record
            Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                    TransactionType.DEPOSIT, amount);
            tx.setToAccountNo(accountNo);

            // Process immediately
            account.deposit(amount);
            tx.setStatus("COMPLETED");  // Mark successful transaction
            account.addTransaction(tx);
            System.out.println("✓ Deposit processed: " + tx.getTxId());
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error processing deposit: " + e.getMessage());
            return false;
        }
    }

    /**
     * Processes a withdrawal transaction immediately.
     * Funds are withdrawn from the account instantly.
     *
     * @param accountNo source account number
     * @param amount withdrawal amount (must be positive)
     * @return true if withdrawal successful, false if account not found or insufficient funds
     */
    public boolean withdraw(String accountNo, double amount) {
        Account account = AccountUtils.findAccount(this.accountList, accountNo);
        if (account == null) {
            System.out.println("✗ Account not found");
            return false;
        }

        try {
            // Create transaction record
            Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                    TransactionType.WITHDRAW, amount);
            tx.setFromAccountNo(accountNo);

            // Process immediately
            if (account.withdraw(amount)) {
                tx.setStatus("COMPLETED");  // Mark successful transaction
                account.addTransaction(tx);
                System.out.println("✓ Withdrawal processed: " + tx.getTxId());
                return true;
            } else {
                tx.setStatus("FAILED");  // Mark failed transaction
                account.addTransaction(tx);  // Record failed attempt for audit trail
                System.out.println("✗ Insufficient funds or withdrawal failed");
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error processing withdrawal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Processes a transfer transaction immediately.
     * Transfers funds from one account to another instantly.
     *
     * @param fromAccountNo source account number
     * @param toAccountNo destination account number
     * @param amount transfer amount (must be positive)
     * @return true if transfer successful, false if accounts not found or insufficient funds
     */
    public boolean transfer(String fromAccountNo, String toAccountNo, double amount) {
        Account fromAccount = AccountUtils.findAccount(this.accountList, fromAccountNo);
        Account toAccount = AccountUtils.findAccount(this.accountList, toAccountNo);

        if (fromAccount == null || toAccount == null) {
            System.out.println("✗ One or both accounts not found");
            return false;
        }

        try {
            // Create transaction record
            Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
                    TransactionType.TRANSFER, amount);
            tx.setFromAccountNo(fromAccountNo);
            tx.setToAccountNo(toAccountNo);

            // Process immediately
            if (fromAccount.withdraw(amount)) {
                toAccount.deposit(amount);
                tx.setStatus("COMPLETED");  // Mark successful transaction
                fromAccount.addTransaction(tx);
                toAccount.addTransaction(tx);
                System.out.println("✓ Transfer processed: " + tx.getTxId());
                return true;
            } else {
                tx.setStatus("FAILED");  // Mark failed transaction
                fromAccount.addTransaction(tx);  // Record failed attempt for audit trail
                System.out.println("✗ Transfer failed: Insufficient funds");
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error processing transfer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves transaction history for a specific account in Stack order (LIFO).
     * Most recent transactions appear first (demonstrates Stack data structure for CC 204).
     *
     * @param accountNo the account number
     * @return Stack of transactions for that account (most recent first)
     */
    public Stack<Transaction> getAccountTransactionsAsStack(String accountNo) {
        Account acc = AccountUtils.findAccount(this.accountList, accountNo);
        if (acc == null) return new Stack<>();

        // Convert LinkedList to Stack (most recent first)
        Stack<Transaction> txStack = new Stack<>();
        LinkedList<Transaction> history = acc.getTransactionHistory();

        // Add all transactions to stack (last added becomes top of stack)
        for (Transaction tx : history) {
            txStack.push(tx);
        }

        return txStack;
    }

    // ===== MENU HANDLER METHODS =====

    /**
     * Handler: Deposit money.
     * Includes access control to prevent customers from depositing to accounts they don't own.
     */
    public void handleDeposit() {
        System.out.println("\n--- DEPOSIT MONEY ---");

        Account account = this.validator.getValidatedAccount(
                "✗ Account not found. Cannot deposit to non-existent account.");
        if (account == null) return;

        // Security check: Verify user can access this account
        if (!this.bankingSystem.canAccessAccount(account.getAccountNo())) {
            System.out.println("✗ Access denied. You can only perform transactions on your own accounts.");
            return;
        }

        Double depAmt = this.validator.getValidatedAmount("Amount");
        if (depAmt == null) return;

        boolean success = this.deposit(account.getAccountNo(), depAmt);
        if (success) {
            System.out.println("✓ Deposit successful!");
            InputValidator.safeLogAction(bankingSystem, "DEPOSIT", "Amount: $" + depAmt + " to account: " + account.getAccountNo());
        } else {
            System.out.println("✗ Deposit failed. Please try again.");
        }
    }

    /**
     * Handler: Withdraw money.
     * Includes access control to prevent customers from withdrawing from accounts they don't own.
     */
    public void handleWithdraw() {
        System.out.println("\n--- WITHDRAW MONEY ---");

        Account account = this.validator.getValidatedAccount(
                "✗ Account not found. Cannot withdraw from non-existent account.");
        if (account == null) return;

        // Security check: Verify user can access this account
        if (!this.bankingSystem.canAccessAccount(account.getAccountNo())) {
            System.out.println("✗ Access denied. You can only perform transactions on your own accounts.");
            return;
        }

        Double witAmt = this.validator.getValidatedAmount("Amount");
        if (witAmt == null) return;

        boolean success = this.withdraw(account.getAccountNo(), witAmt);
        if (success) {
            System.out.println("✓ Withdrawal successful!");
            InputValidator.safeLogAction(bankingSystem, "WITHDRAW", "Amount: $" + witAmt + " from account: " + account.getAccountNo());
        } else {
            System.out.println("✗ Withdrawal failed. Please try again.");
        }
    }

    /**
     * Handler: Transfer money between accounts.
     * Includes access control to prevent customers from transferring FROM accounts they don't own.
     * Customers can transfer TO any account (e.g., paying bills).
     */
    public void handleTransfer() {
        System.out.println("\n--- TRANSFER MONEY ---");

        Account fromAccount = this.validator.getValidatedAccount(
                "✗ Source account not found. Cannot transfer from non-existent account.");
        if (fromAccount == null) return;

        // Security check: Verify user can access SOURCE account
        if (!this.bankingSystem.canAccessAccount(fromAccount.getAccountNo())) {
            System.out.println("✗ Access denied. You can only transfer FROM your own accounts.");
            return;
        }

        Account toAccount = this.validator.getValidatedAccount(
                "✗ Destination account not found. Cannot transfer to non-existent account.");
        if (toAccount == null) return;

        // Check if same account
        if (fromAccount.getAccountNo().equals(toAccount.getAccountNo())) {
            System.out.println("✗ Cannot transfer to the same account");
            return;
        }

        Double amt = this.validator.getValidatedAmount("Amount");
        if (amt == null) return;

        boolean success = this.transfer(fromAccount.getAccountNo(), toAccount.getAccountNo(), amt);
        if (success) {
            System.out.println("✓ Transfer successful!");
            InputValidator.safeLogAction(bankingSystem, "TRANSFER", "Amount: $" + amt + " from " + fromAccount.getAccountNo() + " to " + toAccount.getAccountNo());
        } else {
            System.out.println("✗ Transfer failed. Please try again.");
        }
    }

    /**
     * Handler: View transaction history.
     * Includes access control to prevent customers from viewing transaction history
     * of accounts they don't own.
     */
    public void handleViewTransactionHistory() {
        System.out.println("\n--- VIEW TRANSACTION HISTORY ---");

        Account account = this.validator.getValidatedAccount();
        if (account == null) return;

        // Security check: Verify user can access this account
        if (!this.bankingSystem.canAccessAccount(account.getAccountNo())) {
            System.out.println("✗ Access denied. You can only view transaction history for your own accounts.");
            return;
        }

        // Use Stack to display transactions in LIFO order (most recent first)
        // This demonstrates CC 204 Stack data structure requirement
        Stack<Transaction> txStack = this.getAccountTransactionsAsStack(account.getAccountNo());
        System.out.println("\n=== TRANSACTION HISTORY (LIFO - Most Recent First) ===");
        if (txStack.isEmpty()) {
            System.out.println("No transactions found for this account.");
        } else {
            System.out.println("Displaying " + txStack.size() + " transaction(s) using Stack (LIFO):\n");
            while (!txStack.isEmpty()) {
                System.out.println(txStack.pop());
            }
        }
    }

    // ===== SETUP: BANKING SYSTEM REFERENCE =====

    /**
     * Sets the BankingSystem reference for permission checking and audit logging.
     * Called by BankingSystem after construction to avoid circular dependencies.
     *
     * @param bankingSystem reference to the BankingSystem
     */
    public void setBankingSystem(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }

}
