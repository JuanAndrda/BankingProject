package com.banking.utilities;

import com.banking.models.*;
import java.util.LinkedList;

/**
 * Utility class for account-related operations.
 * Provides centralized, reusable methods for account management.
 */
public class AccountUtils {

    /**
     * Searches for an account by account number.
     * Uses LinkedList with O(n) linear search.
     *
     * @param accountList reference to LinkedList of all accounts
     * @param accountNo the account number to find
     * @return Account object if found, null otherwise
     */
    public static Account findAccount(LinkedList<Account> accountList, String accountNo) {
        for (Account acc : accountList) {
            if (acc.getAccountNo().equals(accountNo)) {
                return acc;
            }
        }
        return null;
    }
}
