package com.banking;

import com.banking.models.*;
import com.banking.auth.*;
import java.util.Scanner;

/**
 * Banking System Application - Entry Point
 *
 * Initializes the Banking System with demo data and launches the interactive console menu.
 *
 * Demo Data:
 * - 3 Customers (Alice, Bob, Charlie)
 * - 4 Accounts (2 Savings, 2 Checking)
 * - 5 Sample transactions (processed via queue)
 *
 * Demonstrates:
 * - Customer and Account creation (CRUD)
 * - 1-to-1 relationship (Customer-Profile)
 * - 1-to-Many relationship (Customer-Accounts)
 * - Transaction queuing and FIFO processing
 * - Interest calculation on Savings accounts
 * - Overdraft protection on Checking accounts
 * - Dependency Injection pattern (Scanner injected to BankingSystem)
 */
public class Main {
    /**
     * Main entry point for the Banking System application.
     * Initializes demo data and starts interactive console menu.
     * Uses Constructor Injection pattern to provide Scanner to BankingSystem.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Create Scanner at entry point (Dependency Injection)
        Scanner sc = new Scanner(System.in);

        try {
            // Inject Scanner into BankingSystem (Constructor Injection)
            BankingSystem bank = new BankingSystem(sc);

            // Initialize demo users and banking data (one-time, at startup)
            registerDemoUsers(bank);
            initializeDemoData(bank);

            // Start application (handles complete lifecycle: login loop, menu, logout/exit)
            bank.startApplication();
        } finally {
            // Guaranteed cleanup of Scanner resource
            sc.close();
        }
    }

    /**
     * Registers demo users (admins and customers) in the authentication system.
     * Creates 2 admin accounts and 3 customer accounts for testing.
     *
     * @param bank the BankingSystem instance
     */
    private static void registerDemoUsers(BankingSystem bank) {
        System.out.println("=== INITIALIZING DEMO USERS ===\n");
        if (bank.registerUser(new Admin("admin", "admin123"))) {
            System.out.println("✓ Admin user created (username: admin / password: admin123)");
        } else {
            System.out.println("✗ Failed to create admin user (username: admin)");
        }

        if (bank.registerUser(new Admin("alice_admin", "pass123"))) {
            System.out.println("✓ Admin user created (username: alice_admin / password: pass123)");
        } else {
            System.out.println("✗ Failed to create admin user (username: alice_admin)");
        }

        // Customer login accounts linked to their customer IDs
        if (bank.registerUser(new UserAccount("alice", "alice123", "C001"))) {
            System.out.println("✓ Customer account created (username: alice / password: alice123)");
        } else {
            System.out.println("✗ Failed to create customer account (username: alice)");
        }

        if (bank.registerUser(new UserAccount("bob", "bob123", "C002"))) {
            System.out.println("✓ Customer account created (username: bob / password: bob123)");
        } else {
            System.out.println("✗ Failed to create customer account (username: bob)");
        }

        if (bank.registerUser(new UserAccount("charlie", "charlie123", "C003"))) {
            System.out.println("✓ Customer account created (username: charlie / password: charlie123)");
        } else {
            System.out.println("✗ Failed to create customer account (username: charlie)");
        }
    }

    /**
     * Initializes demo banking data including customers, profiles, accounts, and transactions.
     * Creates sample data for testing the system functionality.
     *
     * @param bank the BankingSystem instance
     */
    private static void initializeDemoData(BankingSystem bank) {
        System.out.println("\n=== INITIALIZING BANKING DATA ===\n");

        // Create customers
        Customer c1 = bank.createCustomer("C001", "Alice Johnson");
        Customer c2 = bank.createCustomer("C002", "Bob Smith");
        Customer c3 = bank.createCustomer("C003", "Charlie Brown");

        // Create profiles (one-to-one relationship)
        try {
            CustomerProfile p1 = new CustomerProfile("P001", "123 Main St", "5551234567", "alice@email.com");
            c1.setProfile(p1);
            System.out.println("✓ Profile created for Alice");

            CustomerProfile p2 = new CustomerProfile("P002", "456 Oak Ave", "5552345678", "bob@email.com");
            c2.setProfile(p2);
            System.out.println("✓ Profile created for Bob");

            CustomerProfile p3 = new CustomerProfile("P003", "789 Pine Rd", "5553456789", "charlie@email.com");
            c3.setProfile(p3);
            System.out.println("✓ Profile created for Charlie");
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Error creating profile: " + e.getMessage());
        }

        // Create accounts
        createDemoAccounts(bank);

        // Process sample transactions
        processSampleTransactions(bank);

        System.out.println("\n✓ Demo data loaded successfully!");
        System.out.println("  • 3 Customers with login credentials (alice, bob, charlie)");
        System.out.println("  • 7 Accounts total (alice: 3, bob: 2, charlie: 2)");
        System.out.println("  • 5 Sample transactions processed\n");
    }

    /**
     * Creates demo accounts (7 total: 3 for Alice, 2 for Bob, 2 for Charlie).
     * Accounts ACC001-ACC007 all belong to their respective customers.
     *
     * Alice (C001): ACC001 (Savings), ACC002 (Checking), ACC005 (Savings)
     * Bob (C002): ACC003 (Savings), ACC006 (Savings)
     * Charlie (C003): ACC004 (Checking), ACC007 (Checking)
     *
     * @param bank the BankingSystem instance
     */
    private static void createDemoAccounts(BankingSystem bank) {
        // Alice's accounts
        Account acc1 = bank.createAccount("C001", "SAVINGS", "ACC001");
        if (acc1 != null) {
            System.out.println("✓ Account ACC001 (Alice's Savings) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC001");
        }

        Account acc2 = bank.createAccount("C001", "CHECKING", "ACC002");
        if (acc2 != null) {
            System.out.println("✓ Account ACC002 (Alice's Checking) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC002");
        }

        // Bob's accounts
        Account acc3 = bank.createAccount("C002", "SAVINGS", "ACC003");
        if (acc3 != null) {
            System.out.println("✓ Account ACC003 (Bob's Savings) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC003");
        }

        // Charlie's accounts
        Account acc4 = bank.createAccount("C003", "CHECKING", "ACC004");
        if (acc4 != null) {
            System.out.println("✓ Account ACC004 (Charlie's Checking) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC004");
        }

        // Alice's additional accounts
        Account acc5 = bank.createAccount("C001", "SAVINGS", "ACC005");
        if (acc5 != null) {
            System.out.println("✓ Account ACC005 (Alice's Savings) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC005");
        }

        // Bob's additional accounts
        Account acc6 = bank.createAccount("C002", "SAVINGS", "ACC006");
        if (acc6 != null) {
            System.out.println("✓ Account ACC006 (Bob's Savings) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC006");
        }

        // Charlie's additional accounts
        Account acc7 = bank.createAccount("C003", "CHECKING", "ACC007");
        if (acc7 != null) {
            System.out.println("✓ Account ACC007 (Charlie's Checking) created successfully");
        } else {
            System.out.println("✗ Failed to create account ACC007");
        }
    }

    /**
     * Processes sample transactions (deposits, withdrawals, transfers) for testing.
     *
     * @param bank the BankingSystem instance
     */
    private static void processSampleTransactions(BankingSystem bank) {
        System.out.println("\n--- Processing Sample Transactions ---");
        if (bank.deposit("ACC001", 1000.0)) {
            System.out.println("✓ Deposit of $1000.0 to ACC001 successful");
        } else {
            System.out.println("✗ Deposit to ACC001 failed");
        }

        if (bank.deposit("ACC002", 500.0)) {
            System.out.println("✓ Deposit of $500.0 to ACC002 successful");
        } else {
            System.out.println("✗ Deposit to ACC002 failed");
        }

        if (bank.deposit("ACC003", 2000.0)) {
            System.out.println("✓ Deposit of $2000.0 to ACC003 successful");
        } else {
            System.out.println("✗ Deposit to ACC003 failed");
        }

        if (bank.withdraw("ACC002", 100.0)) {
            System.out.println("✓ Withdrawal of $100.0 from ACC002 successful");
        } else {
            System.out.println("✗ Withdrawal from ACC002 failed");
        }

        if (bank.transfer("ACC003", "ACC001", 300.0)) {
            System.out.println("✓ Transfer of $300.0 from ACC003 to ACC001 successful");
        } else {
            System.out.println("✗ Transfer failed");
        }
    }
}