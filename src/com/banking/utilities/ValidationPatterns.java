package com.banking.utilities;

/**
 * ValidationPatterns Class - Centralized Validation Constants
 *
 * This class consolidates all validation patterns, formats, and error messages
 * used throughout the Banking System. This follows the DRY (Don't Repeat Yourself)
 * principle by providing a single source of truth for all validation rules.
 *
 * Benefits:
 * - Single location to change validation rules
 * - Consistent error messages throughout application
 * - Easier maintenance and updates
 * - Reduces code duplication
 *
 * Data Structures: None (purely constants)
 */
public class ValidationPatterns {

    // ===== CUSTOMER ID VALIDATION =====
    /** Customer ID format pattern: C### (e.g., C001) - Anchored with ^ and $ for complete match */
    public static final String CUSTOMER_ID_PATTERN = "^C\\d{3}$";
    /** Customer ID format description for user display */
    public static final String CUSTOMER_ID_FORMAT = "C###";
    /** Customer ID validation error message */
    public static final String CUSTOMER_ID_ERROR =
        "Customer ID must be format C### (e.g., C001)";

    // ===== ACCOUNT NUMBER VALIDATION =====
    /** Account number format pattern: ACC### (e.g., ACC001) - Anchored with ^ and $ for complete match */
    public static final String ACCOUNT_NO_PATTERN = "^ACC\\d{3}$";
    /** Account number format description for user display */
    public static final String ACCOUNT_NO_FORMAT = "ACC###";
    /** Account number validation error message */
    public static final String ACCOUNT_NO_ERROR =
        "Account number must be format ACC### (e.g., ACC001)";

    // ===== PROFILE ID VALIDATION =====
    /** Profile ID format pattern: P### (e.g., P001) - Anchored with ^ and $ for complete match */
    public static final String PROFILE_ID_PATTERN = "^P\\d{3}$";
    /** Profile ID format description for user display */
    public static final String PROFILE_ID_FORMAT = "P###";
    /** Profile ID validation error message */
    public static final String PROFILE_ID_ERROR =
        "Profile ID must be format P### (e.g., P001)";

    // ===== CONTACT INFORMATION VALIDATION =====
    /** Email format pattern for validation */
    public static final String EMAIL_PATTERN =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    /** Email validation error message */
    public static final String EMAIL_ERROR =
        "Invalid email format";
    /** Minimum number of digits required in phone number */
    public static final int PHONE_MIN_DIGITS = 10;
    /** Phone number validation error message */
    public static final String PHONE_ERROR =
        "Phone must contain at least 10 digits";

    // ===== TRANSACTION VALIDATION =====
    /** Transaction status format pattern: COMPLETED or FAILED - Anchored with ^ and $ for complete match */
    public static final String TRANSACTION_STATUS_PATTERN = "^(COMPLETED|FAILED)$";
    /** Transaction status validation error message */
    public static final String TRANSACTION_STATUS_ERROR =
        "Status must be COMPLETED or FAILED";

    // ===== USER VALIDATION ERROR MESSAGES =====
    /** Username empty validation error */
    public static final String USERNAME_EMPTY_ERROR = "Username cannot be empty";
    /** Password empty validation error */
    public static final String PASSWORD_EMPTY_ERROR = "Password cannot be empty";
    /** User role null validation error */
    public static final String USER_ROLE_NULL_ERROR = "User role cannot be null";

    // ===== CUSTOMER VALIDATION ERROR MESSAGES =====
    /** Customer name empty validation error */
    public static final String CUSTOMER_NAME_EMPTY_ERROR = "Name cannot be empty";
    /** Customer null validation error */
    public static final String CUSTOMER_NULL_ERROR = "Customer cannot be null";

    // ===== CUSTOMER PROFILE VALIDATION ERROR MESSAGES =====
    /** Address empty validation error */
    public static final String ADDRESS_EMPTY_ERROR = "Address cannot be empty";

    // ===== ACCOUNT VALIDATION ERROR MESSAGES =====
    /** Account owner null validation error */
    public static final String ACCOUNT_OWNER_NULL_ERROR = "Account must have an owner";
    /** Interest rate range validation error */
    public static final String INTEREST_RATE_RANGE_ERROR = "Interest rate must be between 0 and 1 (0% to 100%)";
    /** Overdraft limit negative validation error */
    public static final String OVERDRAFT_NEGATIVE_ERROR = "Overdraft limit cannot be negative";

    // ===== TRANSACTION VALIDATION ERROR MESSAGES =====
    /** Transaction ID empty validation error */
    public static final String TRANSACTION_ID_EMPTY_ERROR = "Transaction ID cannot be empty";
    /** Account number format error (for invalid format in transactions) */
    public static final String ACCOUNT_NO_FORMAT_ERROR = "Invalid account number format";
    /** Transaction type null validation error */
    public static final String TRANSACTION_TYPE_NULL_ERROR = "Transaction type cannot be null";
    /** Amount positive validation error */
    public static final String AMOUNT_POSITIVE_ERROR = "Amount must be positive";

    // ===== AUDIT LOG VALIDATION ERROR MESSAGES =====
    /** Audit log action empty validation error */
    public static final String ACTION_EMPTY_ERROR = "Action cannot be empty";
    /** Audit log details null validation error */
    public static final String DETAILS_NULL_ERROR = "Details cannot be null";

    /**
     * Private constructor to prevent instantiation of utility class.
     *
     * This utility class provides only static methods and constants for validation.
     * Creating instances is unnecessary and potentially confusing. By declaring the
     * constructor as private, we prevent accidental instantiation and make the
     * class's intended usage clear to developers.
     *
     * Pattern: Utility classes (Math, Collections, Arrays, etc.) use this same approach.
     *
     * This is defensive programming - ensures ValidationPatterns is used correctly
     * throughout the codebase.
     */
    private ValidationPatterns() {
        // Utility class - no instances needed
    }

    /**
     * Validates that a string matches the specified regex pattern.
     * Null values are treated as invalid.
     *
     * @param value the string to validate
     * @param pattern the regex pattern to match
     * @return true if value is non-null and matches the pattern, false otherwise
     */
    public static boolean matchesPattern(String value, String pattern) {
        return value != null && value.matches(pattern);
    }

    /**
     * Validates that a phone number contains at least the minimum digits.
     *
     * @param phone the phone number to validate
     * @return true if phone contains at least PHONE_MIN_DIGITS digits, false otherwise
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.replaceAll("[^0-9]", "").length() >= PHONE_MIN_DIGITS;
    }
}
