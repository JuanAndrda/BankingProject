package com.banking;

import com.banking.models.*;
import com.banking.managers.*;
import com.banking.auth.*;
import com.banking.utilities.*;
import java.util.*;

/**
 * Banking Management System - Main Controller Class
 *
 * This class demonstrates BOTH types of POLYMORPHISM:
 *
 * 1. COMPILE-TIME POLYMORPHISM (Method Overloading):
 *    - Account.withdraw() and other overloaded methods in manager classes
 *    - The compiler decides which method to call based on the arguments.
 *
 * 2. RUNTIME POLYMORPHISM (Method Override):
 *    - Account.withdraw() is overridden by SavingsAccount.withdraw() and CheckingAccount.withdraw()
 *    - Account.getDetails() is overridden by SavingsAccount.getDetails() and CheckingAccount.getDetails()
 *    - The JVM decides which method to call based on the actual object type at runtime.
 *
 * INHERITANCE is demonstrated through:
 *    - Account (parent) → SavingsAccount, CheckingAccount (children)
 *
 * ABSTRACTION is demonstrated through:
 *    - Account is abstract, cannot be instantiated directly
 *    - Must create SavingsAccount or CheckingAccount
 *
 * COMPOSITION is demonstrated through:
 *    - BankingSystem HAS-A InputValidator (NOT IS-A)
 *    - BankingSystem HAS-A CustomerManager
 *    - BankingSystem HAS-A AccountManager
 *    - BankingSystem HAS-A TransactionProcessor
 *
 * This refactoring shows the SINGLE RESPONSIBILITY PRINCIPLE:
 *    - BankingSystem: Orchestrates and manages main menu
 *    - InputValidator: All input validation logic
 *    - CustomerManager: All customer operations
 *    - AccountManager: All account operations
 *    - TransactionProcessor: All transaction operations
 */
public class BankingSystem {
    // ===== SHARED DATA COLLECTIONS =====
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;

    // ===== MANAGER OBJECTS (COMPOSITION) =====
    private InputValidator validator;
    private CustomerManager customerMgr;
    private AccountManager accountMgr;
    private TransactionProcessor txProcessor;
    private AuthenticationManager authManager;  // Handles login and permissions

    // ===== I/O RESOURCE =====
    private Scanner sc;  // Scanner for user input (injected via constructor)
    private User currentUser;  // Currently logged-in user

    /**
     * Constructor: Initialize all manager objects with shared collections and Scanner.
     * Uses Constructor Injection pattern for Scanner (Dependency Injection).
     *
     * @param sc Scanner instance for user input (required for interactive menu)
     */
    public BankingSystem(Scanner sc) {
        // Store injected Scanner
        this.sc = sc;

        // Initialize shared collections
        this.customers = new LinkedList<>();
        this.accountList = new LinkedList<>();

        // Initialize manager objects with shared collections and Scanner
        this.validator = new InputValidator(this.customers, this.accountList, sc);
        this.customerMgr = new CustomerManager(this.customers, this.accountList, this.validator);
        this.accountMgr = new AccountManager(this.customers, this.accountList, this.validator);
        this.txProcessor = new TransactionProcessor(this.accountList, this.validator);

        // Set BankingSystem references in managers for permission checking and audit logging
        this.customerMgr.setBankingSystem(this);
        this.customerMgr.setAccountManager(this.accountMgr);  // For onboarding workflow
        this.accountMgr.setBankingSystem(this);
        this.txProcessor.setBankingSystem(this);

        // Initialize authentication manager
        this.authManager = new AuthenticationManager();
        this.currentUser = null;
    }

    // ===== ROLE-BASED MENU DISPLAY METHODS =====
    /**
     * Displays the admin menu with all available options.
     * Shows customer operations, account operations, profile operations, and reports.
     * No conditional checks - all options displayed for admin users.
     */
    private void displayAdminMenu() {
        System.out.println("│ FULL ADMIN MENU");
        System.out.println("│");

        // Customer Operations
        System.out.println("│ CUSTOMER OPERATIONS");
        System.out.println("│  1. Create Customer");
        System.out.println("│  2. View Customer Details");
        System.out.println("│  3. View All Customers");
        System.out.println("│  4. Delete Customer");
        System.out.println("│");

        // Account Operations
        System.out.println("│ ACCOUNT OPERATIONS");
        System.out.println("│  5. Create Account");
        System.out.println("│  6. View Account Details");
        System.out.println("│  7. View All Accounts");
        System.out.println("│  8. Delete Account");
        System.out.println("│  9. Update Overdraft Limit (Checking)");
        System.out.println("│");

        // Profile Operations
        System.out.println("│ PROFILE OPERATIONS");
        System.out.println("│  14. Create/Update Customer Profile");
        System.out.println("│  15. Update Profile Information");
        System.out.println("│");

        // Reports & Utilities
        System.out.println("│ REPORTS & UTILITIES");
        System.out.println("│  16. Apply Interest (All Savings Accounts)");
        System.out.println("│  17. Sort Accounts by Name");
        System.out.println("│  18. Sort Accounts by Balance");
        System.out.println("│  19. View Audit Trail (Admin Only)");
        System.out.println("│");

        // Session Management
        System.out.println("│ SESSION MANAGEMENT");
        System.out.println("│  0. Logout (Return to Login)");
        System.out.println("│  20. Exit Application");
    }

    /**
     * Displays the customer menu with limited options (ATM mode).
     * Shows only account and transaction operations.
     * No conditional checks - only relevant options displayed for customer users.
     */
    private void displayCustomerMenu() {
        System.out.println("│ TRANSACTION MENU (ATM Mode)");
        System.out.println("│");

        // Account Operations (only View Account Details)
        System.out.println("│ ACCOUNT OPERATIONS");
        System.out.println("│  6. View Account Details");
        System.out.println("│");

        // Transaction Operations
        System.out.println("│ TRANSACTION OPERATIONS");
        System.out.println("│  10. Deposit Money");
        System.out.println("│  11. Withdraw Money");
        System.out.println("│  12. Transfer Money");
        System.out.println("│  13. View Transaction History");
        System.out.println("│");

        // Security Operations
        System.out.println("│ SECURITY OPERATIONS");
        System.out.println("│  21. Change Password");
        System.out.println("│");

        // Session Management
        System.out.println("│ SESSION MANAGEMENT");
        System.out.println("│  0. Logout (Return to Login)");
        System.out.println("│  20. Exit Application");
    }

    // ===== MAIN ENTRY POINT: CONSOLE MENU =====
    /**
     * Runs the interactive console menu for the Banking System.
     * Displays role-based menu options for all CRUD and transaction operations.
     * Uses switch statement to route to appropriate manager handler methods.
     * Scanner is injected via constructor (Dependency Injection).
     *
     * This method demonstrates:
     * - FACADE PATTERN: Simplified interface to complex subsystems
     * - RBAC (Role-Based Access Control): Different menus for different roles
     * - Polymorphic behavior: Different operations available per role
     *
     * @return MenuAction indicating whether user chose LOGOUT or EXIT_APPLICATION
     */
    public MenuAction runConsoleMenu() {
        // Require login before accessing menu
        User loggedInUser = this.login();
        if (loggedInUser == null) {
            System.out.println("✗ Unable to proceed without authentication.");
            return MenuAction.EXIT_APPLICATION;  // Exit after failed login attempts
        }

        // Scanner already initialized from constructor (via Dependency Injection)
        boolean running = true;

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║   BANKING MANAGEMENT SYSTEM v2.0   ║");
        System.out.println("╚════════════════════════════════════╝\n");

        while (running) {
            System.out.println("\n┌─ MAIN MENU ─────────────────────────────────────");

            // Display menu based on user role (no conditional checks needed)
            if (this.currentUser.getUserRole() == UserRole.ADMIN) {
                displayAdminMenu();
            } else {
                displayCustomerMenu();
            }

            System.out.println("└─────────────────────────────────────────────────");
            System.out.print("→ Enter choice: ");

            try {
                int choice = this.sc.nextInt();
                this.sc.nextLine(); // ⚠️ CRITICAL: Consume leftover newline

                System.out.println(); // Add blank line for readability

                switch (choice) {
                    // CUSTOMER OPERATIONS
                    case 1:
                        if (!shouldShowMenuOption(1)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.customerMgr.handleCreateCustomer(this.sc);
                        break;
                    case 2:
                        if (!shouldShowMenuOption(2)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.customerMgr.handleViewCustomerDetails();
                        break;
                    case 3:
                        if (!shouldShowMenuOption(3)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.customerMgr.handleViewAllCustomers();
                        break;
                    case 4:
                        if (!shouldShowMenuOption(4)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.customerMgr.handleDeleteCustomer();
                        break;

                    // ACCOUNT OPERATIONS
                    case 5:
                        if (!shouldShowMenuOption(5)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleCreateAccount(this.sc);
                        break;
                    case 6:
                        if (!shouldShowMenuOption(6)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleViewAccountDetails(this.sc);
                        break;
                    case 7:
                        if (!shouldShowMenuOption(7)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleViewAllAccounts();
                        break;
                    case 8:
                        if (!shouldShowMenuOption(8)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleDeleteAccount(this.sc);
                        break;
                    case 9:
                        if (!shouldShowMenuOption(9)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleUpdateOverdraftLimit(this.sc);
                        break;

                    // TRANSACTION OPERATIONS
                    case 10:
                        this.txProcessor.handleDeposit();
                        break;
                    case 11:
                        this.txProcessor.handleWithdraw();
                        break;
                    case 12:
                        this.txProcessor.handleTransfer();
                        break;
                    case 13:
                        this.txProcessor.handleViewTransactionHistory();
                        break;

                    // PROFILE OPERATIONS
                    case 14:
                        if (!shouldShowMenuOption(14)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.customerMgr.handleCreateCustomerProfile();
                        break;
                    case 15:
                        if (!shouldShowMenuOption(15)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.customerMgr.handleUpdateCustomerProfile(this.sc);
                        break;

                    // REPORTS & UTILITIES
                    case 16:
                        if (!shouldShowMenuOption(16)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleApplyInterest();
                        break;
                    case 17:
                        if (!shouldShowMenuOption(17)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleSortByName();
                        break;
                    case 18:
                        if (!shouldShowMenuOption(18)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.accountMgr.handleSortByBalance();
                        break;

                    case 19:
                        // Check permission for audit trail (admin only)
                        if (!shouldShowMenuOption(19)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        if (this.hasPermission("VIEW_AUDIT_TRAIL")) {
                            this.displayAuditTrail();
                        } else {
                            System.out.println("✗ You do not have permission to view the audit trail.");
                            if (this.currentUser != null) {
                                this.logAction("VIEW_AUDIT_DENIED", "User attempted to view audit trail without permission");
                            }
                        }
                        break;

                    case 21:
                        if (!shouldShowMenuOption(21)) {
                            System.out.println("✗ This option is not available for your account type.");
                            break;
                        }
                        this.handleChangePassword();
                        break;

                    case 0:
                        System.out.println("\n✓ Logging out...");
                        System.out.println("   Please wait while we log you out...\n");
                        this.logout();
                        return MenuAction.LOGOUT;  // Return to login screen

                    case 20:
                        System.out.println("\n✓ Thank you for using the Banking System!");
                        System.out.println("   Goodbye!\n");
                        this.logout();
                        return MenuAction.EXIT_APPLICATION;  // Exit program

                    default:
                        System.out.println("✗ Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
                this.sc.nextLine(); // clear buffer
            }
        }
        // This should not be reached, but return default action
        return MenuAction.LOGOUT;
    }

    // ===== APPLICATION LIFECYCLE =====

    /**
     * Starts the banking application with multi-user login loop.
     *
     * Handles the complete application lifecycle:
     * - Display login screen
     * - Call runConsoleMenu() for user to login and perform operations
     * - Check MenuAction result to determine next step:
     *   - If LOGOUT: Loop back to login screen (next user can login)
     *   - If EXIT_APPLICATION: Exit loop and terminate application
     *
     * This is the main entry point after demo data initialization.
     */
    public void startApplication() {
        boolean appRunning = true;

        while (appRunning) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║      BANKING SYSTEM - LOGIN         ║");
            System.out.println("╚════════════════════════════════════╝");

            // Run console menu (handles login + menu + returns action)
            MenuAction action = runConsoleMenu();

            // Check what user chose
            if (action == MenuAction.EXIT_APPLICATION) {
                appRunning = false;  // Exit the login loop
            }
            // If action is LOGOUT, loop continues and user sees login screen again
        }

        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║        Thank you. Goodbye!         ║");
        System.out.println("╚════════════════════════════════════╝\n");
    }

    // ===== MENU HELPER: ROLE-BASED ACCESS FILTERING =====

    /**
     * Maps a menu option number to the permission required to access it.
     * This is the single source of truth for which permission each menu option needs.
     *
     * @param optionNumber the menu option number
     * @return the permission string required, or null if option doesn't require permission
     */
    private String getPermissionForOption(int optionNumber) {
        return switch(optionNumber) {
            // Session management
            case 0 -> "LOGOUT";
            case 20 -> "EXIT_APP";

            // Customer operations
            case 1 -> "CREATE_CUSTOMER";
            case 2 -> "VIEW_CUSTOMER";
            case 3 -> "VIEW_ALL_CUSTOMERS";
            case 4 -> "DELETE_CUSTOMER";

            // Account operations
            case 5 -> "CREATE_ACCOUNT";
            case 6 -> "VIEW_ACCOUNT_DETAILS";
            case 7 -> "VIEW_ALL_ACCOUNTS";
            case 8 -> "DELETE_ACCOUNT";
            case 9 -> "UPDATE_OVERDRAFT";

            // Transaction operations
            case 10 -> "DEPOSIT";
            case 11 -> "WITHDRAW";
            case 12 -> "TRANSFER";
            case 13 -> "VIEW_TRANSACTION_HISTORY";

            // Profile operations
            case 14 -> "CREATE_PROFILE";
            case 15 -> "UPDATE_PROFILE";

            // Reporting & utilities
            case 16 -> "APPLY_INTEREST";
            case 17 -> "SORT_BY_NAME";
            case 18 -> "SORT_BY_BALANCE";
            case 19 -> "VIEW_AUDIT_TRAIL";

            // Security operations
            case 21 -> "CHANGE_PASSWORD";

            default -> null;
        };
    }

    /**
     * Determines if a menu option should be displayed based on user permissions.
     * Uses the permission system (role-based access control) to determine visibility.
     * Each role (Admin, Customer) has specific permissions defined in their getPermissions() method.
     *
     * Permission-based approach: Single source of truth in getPermissionForOption()
     * Prevents permission duplication and makes maintenance easier.
     *
     * @param optionNumber the menu option number to check
     * @return true if user has the required permission for this option, false otherwise
     */
    private boolean shouldShowMenuOption(int optionNumber) {
        if (this.currentUser == null) {
            return false;
        }

        String requiredPermission = getPermissionForOption(optionNumber);
        if (requiredPermission == null) {
            return false;
        }

        return this.currentUser.getPermissions().contains(requiredPermission);
    }

    // ===== AUTHENTICATION FLOW =====

    /**
     * Initiates the login process.
     * Prompts user for username/password with 3-attempt limit.
     *
     * @return the logged-in User, or null if login failed
     */
    public User login() {
        this.currentUser = this.authManager.login(this.sc);
        return this.currentUser;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        this.authManager.logout();
        this.currentUser = null;
    }

    /**
     * Registers a new user in the authentication system.
     * Used during demo data setup to create admin and customer accounts.
     *
     * @param user the user to register
     * @return true if registration successful
     */
    public boolean registerUser(User user) {
        return this.authManager.registerUser(user);
    }

    /**
     * Returns the currently logged-in user.
     */
    public User getCurrentUser() {
        return this.currentUser;
    }

    /**
     * Returns the authentication manager (for username/password generation).
     * Used by customer and account managers for auto-generation.
     */
    public AuthenticationManager getAuthenticationManager() {
        return this.authManager;
    }

    // ===== AUTHORIZATION =====

    /**
     * Checks if current user has a specific permission.
     */
    public boolean hasPermission(String permission) {
        return this.authManager.hasPermission(permission);
    }

    /**
     * Checks if the current user can access a specific account.
     * Implements role-based access control:
     * - ADMIN users can access ANY account
     * - CUSTOMER users can only access their own linked account
     *
     * This is the centralized access control method - all access checks should use this.
     * Prevents customers from viewing/modifying other customers' accounts.
     *
     * @param accountNo the account number to check access for
     * @return true if current user can access this account, false otherwise
     */
    public boolean canAccessAccount(String accountNo) {
        if (this.currentUser == null) {
            return false;
        }

        User user = this.currentUser;

        // Admins can access any account
        if (user.getUserRole() == UserRole.ADMIN) {
            return true;
        }

        // Customers can access accounts owned by their linked customer
        if (user instanceof UserAccount) {
            UserAccount customerAccount = (UserAccount) user;
            String linkedCustomerId = customerAccount.getLinkedCustomerId();

            // Find the account and check if its owner matches the linked customer ID
            Account account = AccountUtils.findAccount(this.accountList, accountNo);
            if (account != null && account.getOwner() != null) {
                return account.getOwner().getCustomerId().equals(linkedCustomerId);
            }
            return false;
        }

        return false;
    }

    // ===== BUSINESS OPERATIONS (DELEGATED) =====
    // These methods delegate to the appropriate manager objects.
    // This allows Main.java to call simple methods without needing to access managers directly.

    /**
     * Creates a new customer (delegates to CustomerManager).
     */
    public Customer createCustomer(String customerId, String name) {
        return this.customerMgr.createCustomer(customerId, name);
    }

    /**
     * Creates a new account (delegates to AccountManager).
     */
    public Account createAccount(String customerId, String accountType, String accountNo) {
        return this.accountMgr.createAccount(customerId, accountType, accountNo);
    }

    /**
     * Deposits money into an account (delegates to TransactionProcessor).
     */
    public boolean deposit(String accountNo, double amount) {
        return this.txProcessor.deposit(accountNo, amount);
    }

    /**
     * Withdraws money from an account (delegates to TransactionProcessor).
     */
    public boolean withdraw(String accountNo, double amount) {
        return this.txProcessor.withdraw(accountNo, amount);
    }

    /**
     * Transfers money between two accounts (delegates to TransactionProcessor).
     */
    public boolean transfer(String fromAccountNo, String toAccountNo, double amount) {
        return this.txProcessor.transfer(fromAccountNo, toAccountNo, amount);
    }

    // ===== AUDIT & ADMIN UTILITIES =====

    /**
     * Logs an action to the audit trail.
     */
    public void logAction(String action, String details) {
        if (currentUser != null) {
            this.authManager.logAction(currentUser.getUsername(), currentUser.getUserRole(), action, details);
        }
    }

    /**
     * Handler: Change password for current user.
     * Customers must change their auto-generated password on first login.
     * Uses AuthenticationManager to create new User object (immutable pattern).
     */
    private void handleChangePassword() {
        System.out.println("\n--- CHANGE PASSWORD ---");

        // Get current user
        if (this.currentUser == null) {
            System.out.println("✗ No user logged in");
            return;
        }

        String username = this.currentUser.getUsername();

        // Step 1: Prompt for current password (uses InputValidator for consistent pattern)
        String oldPassword = this.validator.getCancellableInput("Enter current password");
        if (oldPassword == null) {
            return;  // User cancelled
        }

        // Step 2: Prompt for new password (uses InputValidator for consistent pattern)
        String newPassword = this.validator.getCancellableInput("Enter new password");
        if (newPassword == null) {
            return;  // User cancelled
        }

        // Step 3: Call AuthenticationManager to change password (creates new User)
        User newUser = this.authManager.changePassword(username, oldPassword, newPassword);

        if (newUser == null) {
            System.out.println("✗ Password change failed. Please try again.");
            return;
        }

        // Step 4: Update currentUser reference to new User object
        this.currentUser = newUser;

        // Step 5: Set passwordChangeRequired to false (password has been changed)
        this.currentUser.setPasswordChangeRequired(false);

        // Step 6: Success message
        System.out.println("\n✓ Password changed successfully!");
        System.out.println("  • You are still logged in with your new password");
        System.out.println("  • New password will be used for all future logins\n");
    }

    /**
     * Displays the audit trail (admin only).
     */
    public void displayAuditTrail() {
        this.authManager.displayAuditTrail();
    }

}
