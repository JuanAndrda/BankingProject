package com.banking.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.banking.utilities.ValidationPatterns;

/**
 * Represents a banking transaction (Deposit, Withdrawal, or Transfer).
 * Immutable once created (except status).
 * Stores transaction details including amounts, accounts, type, and timestamp.
 */
public class Transaction {
    private String txId;                // Format: TX### (e.g., TX001)
    private String fromAccountNo;       // Source account (for withdraw/transfer)
    private String toAccountNo;         // Destination account (for deposit/transfer)
    private TransactionType type;       // DEPOSIT, WITHDRAW, or TRANSFER
    private double amount;              // Transaction amount (positive)
    private LocalDateTime timestamp;    // When transaction was created
    private String status;              // PENDING, COMPLETED, or FAILED

    /**
     * Creates a new transaction with the given ID, type, and amount.
     * Automatically sets timestamp to current time and status to PENDING.
     *
     * @param txId unique transaction ID (format: TX###)
     * @param type transaction type (DEPOSIT, WITHDRAW, TRANSFER)
     * @param amount transaction amount (must be positive)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Transaction(String txId, TransactionType type, double amount) {
        this.setTxId(txId);
        this.setType(type);
        this.setAmount(amount);
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    // ===== GETTERS =====
    /** @return transaction ID */
    public String getTxId() { return this.txId; }

    /** @return source account number (null for deposits) */
    public String getFromAccountNo() { return this.fromAccountNo; }

    /** @return destination account number (null for withdrawals) */
    public String getToAccountNo() { return this.toAccountNo; }

    /** @return transaction type (DEPOSIT, WITHDRAW, TRANSFER) */
    public TransactionType getType() { return this.type; }

    /** @return transaction amount (positive) */
    public double getAmount() { return this.amount; }

    /** @return timestamp when transaction was created */
    public LocalDateTime getTimestamp() { return this.timestamp; }

    /** @return transaction status (PENDING, COMPLETED, FAILED) */
    public String getStatus() { return this.status; }

    // ===== SETTERS WITH VALIDATION =====
    /**
     * Sets and validates the transaction ID.
     *
     * @param txId unique ID (format: TX###)
     * @throws IllegalArgumentException if txId is empty/null
     */
    public void setTxId(String txId) {
        if (txId == null || txId.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.TRANSACTION_ID_EMPTY_ERROR);
        }
        this.txId = txId;
    }

    /**
     * Sets the source account number (for withdrawals and transfers).
     * Validates account number format using ValidationPatterns constant.
     *
     * @param accountNo source account (format: ACC###)
     * @throws IllegalArgumentException if format is invalid
     */
    public void setFromAccountNo(String accountNo) {
        if (accountNo != null && !accountNo.matches(ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_FORMAT_ERROR + ": " + accountNo);
        }
        this.fromAccountNo = accountNo;
    }

    /**
     * Sets the destination account number (for deposits and transfers).
     * Validates account number format using ValidationPatterns constant.
     *
     * @param accountNo destination account (format: ACC###)
     * @throws IllegalArgumentException if format is invalid
     */
    public void setToAccountNo(String accountNo) {
        if (accountNo != null && !accountNo.matches(ValidationPatterns.ACCOUNT_NO_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.ACCOUNT_NO_FORMAT_ERROR + ": " + accountNo);
        }
        this.toAccountNo = accountNo;
    }

    /**
     * Sets and validates the transaction type.
     *
     * @param type transaction type (DEPOSIT, WITHDRAW, TRANSFER)
     * @throws IllegalArgumentException if type is null
     */
    public void setType(TransactionType type) {
        if (type == null) {
            throw new IllegalArgumentException(ValidationPatterns.TRANSACTION_TYPE_NULL_ERROR);
        }
        this.type = type;
    }

    /**
     * Sets and validates the transaction amount.
     *
     * @param amount transaction amount (must be positive)
     * @throws IllegalArgumentException if amount is <= 0
     */
    public void setAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(ValidationPatterns.AMOUNT_POSITIVE_ERROR);
        }
        this.amount = amount;
    }

    /**
     * Sets and validates the transaction status.
     * Only allows valid status values to maintain data integrity.
     * Updated by BankingSystem after transaction processing.
     *
     * @param status transaction status (must be COMPLETED or FAILED)
     * @throws IllegalArgumentException if status is invalid or null
     */
    public void setStatus(String status) {
        if (status == null || !status.matches(ValidationPatterns.TRANSACTION_STATUS_PATTERN)) {
            throw new IllegalArgumentException(ValidationPatterns.TRANSACTION_STATUS_ERROR);
        }
        this.status = status;
    }

    /**
     * Returns a formatted string representation of this transaction.
     * Format: TX[ID] TYPE $AMOUNT [STATUS] TIMESTAMP
     * Uses getters to maintain encapsulation.
     *
     * @return formatted transaction details
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("TX[%s] %s $%.2f [%s] %s",
                this.getTxId(), this.getType(), this.getAmount(), this.getStatus(), this.getTimestamp().format(formatter));
    }
}
