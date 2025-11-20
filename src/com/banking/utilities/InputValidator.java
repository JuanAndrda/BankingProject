package com.banking.utilities;

import com.banking.models.*;
import java.util.*;

/**
 * Input Validation Utility Class
 *
 * This class centralizes all input validation and user interaction logic.
 * It demonstrates the SINGLE RESPONSIBILITY PRINCIPLE by handling only
 * input validation and cancellation concerns.
 *
 * Benefits of this separation:
 * - Easier to test validation logic independently
 * - Reusable across different parts of the system
 * - Clear separation of concerns (validation vs business logic)
 * - Demonstrates proper use of COMPOSITION over inheritance
 */
public class InputValidator {
    private LinkedList<Customer> customers;
    private LinkedList<Account> accountList;
    private Scanner sc;

    /**
     * Constructor: Initialize validator with references to shared collections.
     *
     * @param customers reference to LinkedList of all customers
     * @param accountList reference to LinkedList of all accounts
     * @param sc Scanner for user input
     */
    public InputValidator(LinkedList<Customer> customers, LinkedList<Account> accountList, Scanner sc) {
        this.customers = customers;
        this.accountList = accountList;
        this.sc = sc;
    }

    // ===== HELPER/LOOKUP METHODS (Foundation) =====

    /**
     * Searches for a customer by ID.
     * Uses LinkedList with O(n) linear search.
     *
     * @param customerId the customer ID to find
     * @return Customer object if found, null otherwise
     */
    private Customer findCustomer(String customerId) {
        for (Customer c : this.customers) {
            if (c.getCustomerId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Checks if a profile ID already exists in the system.
     *
     * @param profileId the profile ID to check
     * @return true if profile ID already exists, false otherwise
     */
    public boolean profileIdExists(String profileId) {
        for (Customer c : this.customers) {
            if (c.getProfile() != null && c.getProfile().getProfileId().equals(profileId)) {
                return true;
            }
        }
        return false;
    }

    // ===== LOW-LEVEL INPUT OPERATIONS (Building Blocks) =====

    /**
     * Gets user input with cancellation support.
     * User can type "back" to cancel the operation.
     *
     * @param prompt message to display to user
     * @return user input, or null if user types "back"
     */
    public String getCancellableInput(String prompt) {
        System.out.print(prompt + " (or type 'back' to cancel): ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("back")) {
            System.out.println("✗ Operation cancelled. Returning to menu...\n");
            return null;
        }
        return input;
    }

    /**
     * Gets user input as a double with cancellation support.
     * User can type "back" to cancel the operation.
     * Returns null if input cannot be parsed as double.
     *
     * @param prompt message to display to user
     * @return parsed double, or null if user cancels or invalid format
     */
    public Double getCancellableDouble(String prompt) {
        System.out.print(prompt + " (or type 'back' to cancel): ");
        String input = sc.nextLine().trim();
        if (input.equalsIgnoreCase("back")) {
            System.out.println("✗ Operation cancelled. Returning to menu...\n");
            return null;
        }
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format");
            return null;
        }
    }

    // ===== MEDIUM-LEVEL VALIDATION (Composed Methods) =====

    /**
     * Gets user input matching a specific pattern, with validation.
     * Allows user to cancel operation by typing "back".
     *
     * @param prompt message to display to user
     * @param pattern regex pattern that input must match
     * @param formatHint hint about correct format
     * @return validated input string, or null if user cancels
     */
    public String getValidatedInput(String prompt, String pattern, String formatHint) {
        while (true) {
            String input = this.getCancellableInput(prompt + " " + formatHint);
            if (input == null) return null;  // User cancelled

            if (input.matches(pattern)) {
                return input;
            } else {
                System.out.println("✗ Invalid format! " + formatHint);
                System.out.println("   Please try again...\n");
            }
        }
    }

    /**
     * Gets and validates a non-empty string from user.
     * Allows user to cancel operation by typing "back".
     *
     * @param prompt message to display to user
     * @return non-empty string, or null if user cancels
     */
    public String getValidatedString(String prompt) {
        while (true) {
            String input = this.getCancellableInput(prompt);
            if (input == null) return null;

            if (!input.trim().isEmpty()) {
                return input.trim();
            } else {
                System.out.println("✗ This field cannot be empty!");
                System.out.println("   Please try again...\n");
            }
        }
    }

    /**
     * Gets and validates a positive decimal amount from user.
     * Allows user to cancel operation by typing "back".
     *
     * @param prompt message to display to user
     * @return positive double amount, or null if user cancels or invalid
     */
    public Double getValidatedAmount(String prompt) {
        while (true) {
            Double amount = this.getCancellableDouble(prompt);
            if (amount == null) return null;

            if (amount > 0) {
                return amount;
            } else {
                System.out.println("✗ Amount must be greater than zero!");
                System.out.println("   Please try again...\n");
            }
        }
    }

    /**
     * Gets and validates account type (SAVINGS or CHECKING).
     * Allows user to cancel operation by typing "back".
     *
     * @param prompt message to display to user
     * @return "SAVINGS" or "CHECKING" (uppercase), or null if user cancels
     */
    public String getValidatedAccountType(String prompt) {
        while (true) {
            String type = this.getCancellableInput(prompt);
            if (type == null) return null;

            if (type.equalsIgnoreCase("SAVINGS") || type.equalsIgnoreCase("CHECKING")) {
                return type.toUpperCase();
            } else {
                System.out.println("✗ Invalid account type! Must be either SAVINGS or CHECKING");
                System.out.println("   Please try again...\n");
            }
        }
    }

    // ===== HIGH-LEVEL CUSTOMER OPERATIONS (Uses Helpers) =====

    /**
     * Gets and validates a customer from user input (default error message).
     * This is METHOD OVERLOADING - same name, different parameters.
     *
     * @return Customer if found, null if cancelled or not found
     */
    public Customer getValidatedCustomer() {
        return this.getValidatedCustomer("✗ Customer not found");
    }

    /**
     * Gets and validates a customer from user input (custom error message).
     * This is METHOD OVERLOADING - same name as above, but with extra parameter.
     * Demonstrates COMPILE-TIME POLYMORPHISM.
     *
     * @param errorMessage custom message to display if customer not found
     * @return Customer if found, null if cancelled or not found
     */
    public Customer getValidatedCustomer(String errorMessage) {
        String custId = this.getValidatedInput("Customer ID",
                ValidationPatterns.CUSTOMER_ID_PATTERN,
                "(format: " + ValidationPatterns.CUSTOMER_ID_FORMAT + " e.g., C001)");
        if (custId == null) return null;  // User cancelled

        Customer customer = this.findCustomer(custId);
        if (customer == null) {
            System.out.println(errorMessage);
        }
        return customer;
    }

    /**
     * Gets and validates an account from user input (default error message).
     * This is METHOD OVERLOADING - same name, different parameters.
     *
     * @return Account if found, null if cancelled or not found
     */
    public Account getValidatedAccount() {
        return this.getValidatedAccount("✗ Account not found");
    }

    /**
     * Gets and validates an account from user input (custom error message).
     * This is METHOD OVERLOADING - same name as above, but with extra parameter.
     * Demonstrates COMPILE-TIME POLYMORPHISM.
     *
     * @param errorMessage custom message to display if account not found
     * @return Account if found, null if cancelled or not found
     */
    public Account getValidatedAccount(String errorMessage) {
        String accNo = this.getValidatedInput("Account Number",
                ValidationPatterns.ACCOUNT_NO_PATTERN,
                "(format: " + ValidationPatterns.ACCOUNT_NO_FORMAT + " e.g., ACC001)");
        if (accNo == null) return null;  // User cancelled

        Account account = AccountUtils.findAccount(this.accountList, accNo);
        if (account == null) {
            System.out.println(errorMessage);
        }
        return account;
    }

    /**
     * Gets and validates customer, ensuring profile exists.
     *
     * @return Customer with profile, null if cancelled, not found, or no profile
     */
    public Customer getValidatedCustomerWithProfile() {
        Customer customer = this.getValidatedCustomer(
                "✗ Customer not found. Cannot access profile.");
        if (customer == null) return null;

        if (customer.getProfile() == null) {
            System.out.println("✗ Customer has no profile. Please create one first (Option 15)");
            return null;
        }
        return customer;
    }

    /**
     * Gets and validates an account with access control (for customers).
     * Customers can access all accounts owned by their linked customer.
     * Admins can access any account.
     *
     * @param currentUser the currently logged-in user (admin or customer)
     * @return Account if user can access it, null if cancelled, not found, or access denied
     */
    public Account getValidatedAccountWithAccessControl(com.banking.auth.User currentUser) {
        // Check if user is null (no one logged in)
        if (currentUser == null) {
            System.out.println("✗ No user logged in");
            return null;
        }

        // If user is admin, allow any account input
        if (currentUser.getUserRole() == com.banking.auth.UserRole.ADMIN) {
            return this.getValidatedAccount("✗ Account not found");
        }

        // For customers: show their linked customer's accounts and let them choose
        if (currentUser instanceof com.banking.auth.UserAccount) {
            com.banking.auth.UserAccount customerAccount = (com.banking.auth.UserAccount) currentUser;
            String linkedCustomerId = customerAccount.getLinkedCustomerId();

            // Find all accounts owned by this customer
            java.util.LinkedList<com.banking.models.Account> customerAccounts = new java.util.LinkedList<>();
            for (com.banking.models.Account acc : this.accountList) {
                if (acc.getOwner() != null && acc.getOwner().getCustomerId().equals(linkedCustomerId)) {
                    customerAccounts.add(acc);
                }
            }

            // If customer has no accounts, inform them
            if (customerAccounts.isEmpty()) {
                System.out.println("✗ You have no accounts in the system");
                return null;
            }

            // Show available accounts
            System.out.println("\nYour accounts:");
            int index = 1;
            for (com.banking.models.Account acc : customerAccounts) {
                System.out.println("  " + index + ". " + acc.getAccountNo() + " (" + acc.getDetails() + ")");
                index++;
            }

            // Let customer choose an account
            String accNo = this.getValidatedInput("Account Number",
                    ValidationPatterns.ACCOUNT_NO_PATTERN,
                    "(format: " + ValidationPatterns.ACCOUNT_NO_FORMAT + " e.g., ACC001)");
            if (accNo == null) return null;  // User cancelled

            // Verify the chosen account belongs to this customer
            for (com.banking.models.Account acc : customerAccounts) {
                if (acc.getAccountNo().equals(accNo)) {
                    return acc;
                }
            }

            System.out.println("✗ You can only access accounts that belong to you");
            return null;
        }

        System.out.println("✗ Cannot determine user type");
        return null;
    }

    // ===== NEW UTILITY METHODS (Refactoring Extracted Patterns) =====

    /**
     * Prompts user for yes/no confirmation.
     * Consolidates repeated confirmation logic found in 4+ locations.
     * Extracted from: CustomerManager (lines 285, 359, 428), BankingSystem (lines 661, 670)
     *
     * @param message the confirmation message to display
     * @return true if user confirms (yes/y), false otherwise
     */
    public boolean confirmAction(String message) {
        System.out.print(message + " (yes/no): ");
        String response = this.sc.nextLine().trim();
        return response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y");
    }

    /**
     * Safely logs an action to the audit trail with null-safety check.
     * Consolidates repeated logging pattern found in 15+ locations.
     * Extracted from: All manager classes with pattern:
     *     if (bankingSystem != null && bankingSystem.getCurrentUser() != null) {
     *         bankingSystem.logAction(...);
     *     }
     *
     * This wrapper encapsulates the null-check logic, reducing code duplication.
     *
     * @param bankingSystem the BankingSystem instance (may be null)
     * @param action the action description (e.g., "CREATE_CUSTOMER")
     * @param details the action details
     */
    public static void safeLogAction(com.banking.BankingSystem bankingSystem, String action, String details) {
        if (bankingSystem != null && bankingSystem.getCurrentUser() != null) {
            bankingSystem.logAction(action, details);
        }
    }

    /**
     * Gets validated integer input within a range.
     * Used for menu choices and numeric selections.
     *
     * @param prompt message to display to user
     * @return parsed integer, or 0 if user cancels or invalid input
     */
    public int getValidatedInt(String prompt) {
        System.out.print(prompt + " (or type 'back' to cancel): ");
        String input = this.sc.nextLine().trim();
        if (input.equalsIgnoreCase("back")) {
            System.out.println("✗ Operation cancelled. Returning to menu...\n");
            return 0;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid number format");
            return 0;
        }
    }
}
