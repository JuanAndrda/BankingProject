# Data Structures Used in Banking System

## Overview
This document explains each data structure used in the Banking System project, why it was chosen, and how it contributes to real-world banking logic and CC 204 requirements.

---

## 1. LinkedList (Java Collections)

### Where Used
- **BankingSystem.java**:
  - `LinkedList<Customer> customers` - Stores all customers
  - `LinkedList<Account> accountList` - Stores all accounts
- **Account.java**:
  - `LinkedList<Transaction> transactionHistory` - Stores transaction history for each account
- **Customer.java**:
  - `LinkedList<Account> accounts` - Stores accounts owned by each customer

### Data Stored
- Customer objects (ID, name, accounts, profile)
- Account objects (number, balance, owner, transactions)
- Transaction records (ID, type, amount, timestamp, status)

### Why This Data Structure Was Chosen

| Reason | Benefit |
|--------|---------|
| **Maintains Insertion Order** | Preserves creation sequence (FIFO for accounts, chronological for transactions) |
| **Dynamic Size** | Automatically grows as customers/accounts/transactions are added |
| **O(1) Append** | Fast addition of new customers, accounts, or transactions |
| **Real-World Banking** | Banks track accounts in creation order; transaction history is chronological |
| **LinkedList vs ArrayList** | No random access needed; LinkedList prevents overhead of resizing |

### Example Usage in Real-World Banking

```java
// Transaction History - Must maintain chronological order
LinkedList<Transaction> transactionHistory = new LinkedList<>();
transactionHistory.add(new Transaction("TX001", DEPOSIT, 1000.0));  // First transaction
transactionHistory.add(new Transaction("TX002", WITHDRAW, 100.0));   // Second transaction
transactionHistory.add(new Transaction("TX003", TRANSFER, 300.0));   // Third transaction
// History is always in insertion order - perfect for audit trails
```

### Time Complexity Analysis

| Operation | Complexity | Usage |
|-----------|-----------|-------|
| Add (append) | O(1) | Adding new customers/accounts/transactions |
| Remove (from middle) | O(n) | Deleting specific account (need to find first) |
| Find (search) | O(n) | Finding customer/account by ID |
| Iterate (display all) | O(n) | Listing all customers/accounts |

### Why NOT Other Structures?
- **NOT HashMap**: Would lose insertion order (can't maintain customer creation sequence)
- **NOT TreeMap**: PROHIBITED per rubric (no tree-based structures)
- **NOT Array**: Fixed size (number of customers/accounts unknown)

---

## 2. Queue (Java Collections - LinkedList-based)

### Where Used
- **BankingSystem.java**:
  - `Queue<Transaction> txQueue` - FIFO transaction processing queue

### Data Stored
- Transaction objects queued for processing (DEPOSIT, WITHDRAW, TRANSFER)

### Why This Data Structure Was Chosen

| Reason | Benefit |
|--------|---------|
| **FIFO Processing** | Transactions processed in order received (fair, predictable) |
| **Real-World Banking** | Banks process transactions in order (first come, first served) |
| **Asynchronous Processing** | Queue transactions immediately, process them later in batch |
| **Interface-Based** | `Queue<T>` interface allows flexibility (LinkedList, PriorityQueue) |
| **Prevents Race Conditions** | Queuing ensures orderly, sequential transaction processing |

### Example Usage in Real-World Banking

```java
// Customer queues 3 transactions
bank.deposit("ACC001", 1000.0);      // TX001 queued
bank.withdraw("ACC002", 100.0);      // TX002 queued
bank.transfer("ACC001", "ACC002", 300.0); // TX003 queued

// Bank processes all in FIFO order
bank.processTransactionQueue();
// Output:
// ✓ Processed 3 transactions
// TX001 DEPOSIT $1000 [COMPLETED]
// TX002 WITHDRAW $100 [COMPLETED]
// TX003 TRANSFER $300 [COMPLETED]
```

### Why FIFO is Important for Banking
1. **Fairness**: All transactions treated equally
2. **Predictability**: Customers know transaction order
3. **Audit Trail**: Clear sequence for reconciliation
4. **Prevents Overdrafts**: Process oldest transactions first, catch violations early

### Time Complexity Analysis

| Operation | Complexity | Usage |
|-----------|-----------|-------|
| Offer (enqueue) | O(1) | Adding transaction to queue |
| Poll (dequeue) | O(1) | Processing next transaction |
| Peek | O(1) | View next transaction without removing |
| IsEmpty | O(1) | Check if any transactions remain |

### Code Implementation
```java
public Queue<Transaction> txQueue = new LinkedList<>();

// Queueing a transaction
public boolean deposit(String accountNo, double amount) {
    // ... validation ...
    Transaction tx = new Transaction("TX" + String.format("%03d", this.txCounter++),
            TransactionType.DEPOSIT, amount);
    this.txQueue.offer(tx);  // Add to queue (FIFO)
    return true;
}

// Processing all transactions in FIFO order
public void processTransactionQueue() {
    while (!this.txQueue.isEmpty()) {
        Transaction tx = this.txQueue.poll();  // Remove from front (FIFO)
        // ... process transaction ...
    }
}
```

### Why NOT Other Structures?
- **NOT Stack**: Would process transactions LIFO (wrong order)
- **NOT ArrayList**: No FIFO guarantee (would need manual index management)
- **NOT LinkedList directly**: Queue interface makes FIFO contract explicit

---

## 3. Enum (Java Language Feature)

### Where Used
- **TransactionType.java**:
  - `DEPOSIT` - Money added to account
  - `WITHDRAW` - Money removed from account
  - `TRANSFER` - Money moved between accounts

### Data Stored
- Fixed set of transaction type constants

### Why This Data Structure Was Chosen

| Reason | Benefit |
|--------|---------|
| **Type Safety** | Compiler prevents invalid transaction types |
| **No String Errors** | Can't accidentally use "DEPOSITT" (typo) |
| **Readable Code** | `TransactionType.DEPOSIT` is clear vs magic string "DEPOSIT" |
| **Switch Statements** | Java compiler ensures all cases handled |
| **Real-World Banking** | Only 3 valid transaction types in simple banking |

### Example Usage in Real-World Banking

```java
// Type-safe transaction creation
public Transaction(String txId, TransactionType type, double amount) {
    this.type = type;  // Compiler ensures valid type
}

// Switch statement with enum (compiler checks all cases)
public void processTransactionQueue() {
    while (!this.txQueue.isEmpty()) {
        Transaction tx = this.txQueue.poll();

        switch (tx.getType()) {  // Type-safe!
            case DEPOSIT:
                // ... deposit logic ...
                break;
            case WITHDRAW:
                // ... withdraw logic ...
                break;
            case TRANSFER:
                // ... transfer logic ...
                break;
        }
    }
}
```

### Why Enum vs Alternatives

| Alternative | Problem |
|------------|---------|
| **String Constants** | Can pass any string; typos not caught |
| **Integer Constants** | Meaningless numbers (0=?, 1=?) |
| **Class with final fields** | Verbose, unnecessary |

### Code Implementation
```java
enum TransactionType {
    DEPOSIT,    // Money deposited into account
    WITHDRAW,   // Money withdrawn from account
    TRANSFER    // Money transferred between accounts
}

// Usage is type-safe
Transaction tx = new Transaction("TX001", TransactionType.DEPOSIT, 1000.0);
```

---

## 4. Stack (Java Collections - LIFO)

### Where Used (NEW - Nov 13, 2025)
- **TransactionProcessor.java**:
  - `Stack<Transaction>` - Display transaction history in LIFO order (most recent first)

### Data Stored
- Transaction objects from account history (in reverse order)

### Why This Data Structure Was Chosen

| Reason | Benefit |
|--------|---------|
| **LIFO Display** | Users see most recent transactions first (natural for banking) |
| **Real-World Use** | Banks often show recent transactions first in statements |
| **Familiar Pattern** | Stack is well-known data structure (CC 204) |
| **No Tree-Based** | Complies with rubric constraint |
| **Simple Pop Operation** | O(1) removal and display |

### Example Usage in Real-World Banking

```java
// Transaction history in insertion order (oldest to newest)
LinkedList<Transaction> history = new LinkedList<>();
history.add(TX001 - Deposit $1000);   // Day 1
history.add(TX002 - Withdraw $100);   // Day 2
history.add(TX003 - Transfer $300);   // Day 3

// User views history - wants MOST RECENT FIRST
Stack<Transaction> txStack = getAccountTransactionsAsStack(accountNo);
// Stack (LIFO):
// TX003 - Transfer $300   (display first - most recent)
// TX002 - Withdraw $100
// TX001 - Deposit $1000   (display last - oldest)

System.out.println("=== TRANSACTION HISTORY (LIFO - Most Recent First) ===");
while (!txStack.isEmpty()) {
    System.out.println(txStack.pop());  // Pop from stack - LIFO
}
```

### Time Complexity Analysis

| Operation | Complexity | Usage |
|-----------|-----------|-------|
| Push | O(1) | Adding transaction to stack |
| Pop | O(1) | Removing transaction for display |
| Peek | O(1) | View top transaction without removing |
| IsEmpty | O(1) | Check if any transactions remain |

### Code Implementation

```java
// Get transactions as Stack (LIFO order)
private Stack<Transaction> getAccountTransactionsAsStack(String accountNo) {
    Account acc = AccountUtils.findAccount(this.accountList, accountNo);
    if (acc == null) {
        return new Stack<>();
    }

    Stack<Transaction> txStack = new Stack<>();
    // Add all transactions to stack (will be LIFO when popped)
    for (Transaction tx : acc.getTransactionHistory()) {
        txStack.push(tx);
    }
    return txStack;
}

// Display transactions in LIFO order
public void handleViewTransactionHistory(Scanner sc) {
    Account account = this.validator.getValidatedAccount();
    if (account == null) return;

    Stack<Transaction> txStack = this.getAccountTransactionsAsStack(account.getAccountNo());
    System.out.println("=== TRANSACTION HISTORY (LIFO - Most Recent First) ===");
    while (!txStack.isEmpty()) {
        System.out.println(txStack.pop());  // Pop displays in reverse order
    }
}
```

### Why NOT Other Structures?
- **NOT LinkedList directly**: Would show oldest first (wrong order for users)
- **NOT ArrayList**: No LIFO guarantee
- **NOT Queue**: Would show FIFO (oldest first, wrong order)

---

## 5. LinkedList for Permissions (Auth Classes - NEW Nov 13, 2025)

### Where Used
- **User.java (abstract class)**:
  - `abstract LinkedList<String> getPermissions()` - Return user permissions
- **Admin.java**:
  - `LinkedList<String> permissions` - Admin role permissions (20 total)
- **UserAccount.java**:
  - `LinkedList<String> permissions` - Customer role permissions (5 total)

### Data Stored
- Permission strings (e.g., "DEPOSIT", "WITHDRAW", "CREATE_CUSTOMER")

### Why LinkedList (NOT HashSet)?

**Original Design:** HashSet
```java
// ❌ NOT USED ANYMORE
Set<String> permissions = new HashSet<>();  // Fast lookup O(1)
```

**Current Design:** LinkedList
```java
// ✅ CURRENT APPROACH
LinkedList<String> permissions = new LinkedList<>();  // O(n) lookup
```

**Reasoning:**

| Reason | Benefit |
|--------|---------|
| **Consistency (CC 204)** | LinkedList used everywhere in project - familiar to students |
| **Teacher Understanding** | LinkedList discussed in class; HashSet not discussed |
| **Small Permission Set** | 5-20 items; O(n) lookup acceptable |
| **Project Cohesion** | Same data structure pattern across entire system |
| **Simplicity Over Optimization** | Learning clarity more important than micro-optimization |

### Example Usage

```java
// Admin permissions
public LinkedList<String> getPermissions() {
    LinkedList<String> permissions = new LinkedList<>();
    permissions.add("CREATE_CUSTOMER");
    permissions.add("VIEW_CUSTOMER");
    permissions.add("DELETE_CUSTOMER");
    // ... more permissions ...
    return permissions;
}

// Check if user has permission (iterate through LinkedList)
public boolean hasPermission(String permission) {
    if (permission == null) return false;
    for (String p : getPermissions()) {
        if (p.equals(permission)) {
            return true;  // Found permission
        }
    }
    return false;  // Permission not found
}
```

### Time Complexity Analysis

| Operation | Complexity | Usage |
|-----------|-----------|-------|
| Add | O(1) | Adding permission to list |
| Find | O(n) | Checking if user has permission |
| Iterate | O(n) | Displaying all permissions |

### Real-World Banking Context

```java
// Customer (john) trying to create another customer
if (currentUser.hasPermission("CREATE_CUSTOMER")) {
    // Allowed - admin only
    customerManager.handleCreateCustomer(scanner);
} else {
    // Denied - customer doesn't have this permission
    System.out.println("✗ Permission denied: CREATE_CUSTOMER");
}
```

---

## 6. Comparison of All Data Structures Used

### Summary Table

| Structure | Purpose | Location | Type | Real-World Use |
|-----------|---------|----------|------|-----------------|
| **LinkedList** | Store customers, accounts, transactions, permissions, users | BankingSystem, Customer, Account, User, AuthManager | Dynamic collection | Registry, history, audit trail, permission set |
| **Queue** | FIFO transaction processing | BankingSystem | Ordered queue | Transaction pipeline |
| **Stack** | LIFO transaction display | TransactionProcessor | Ordered stack | Display recent transactions first |
| **Enum** | Transaction type classification | TransactionType | Fixed set | Valid transaction categories |

**Total Structures: 4 (no tree-based, per rubric)**

---

## Data Structure Design Decisions

### Why NOT Tree-Based Structures (Per Rubric)
- **TreeMap/TreeSet PROHIBITED**: Can't use for this project
- **Why rubric forbids them**: Rubric emphasizes learning other data structures
- **Alternatives used**: LinkedList for ordering, Collections.sort() for sorting

### Sorting Demonstration (CC 204 Requirement)

```java
// BEFORE SORTING: Natural creation order (LinkedList insertion order)
displayAccountsBeforeSort();
// Output:
// ACC001 | Owner: Alice | Balance: $1300.00 | Interest: 3%
// ACC002 | Owner: Alice | Balance: $500.00 | Overdraft: $500
// ACC003 | Owner: Bob | Balance: $2000.00 | Interest: 3%

// SORTING with custom Comparator
sortAccountsByName();  // Uses Collections.sort() with Lambda

// AFTER SORTING: Alphabetically by customer name
displayAccountsAfterSort();
// Output:
// ACC002 | Owner: Alice | Balance: $500.00 | Overdraft: $500
// ACC001 | Owner: Alice | Balance: $1300.00 | Interest: 3%
// ACC003 | Owner: Bob | Balance: $2000.00 | Interest: 3%
```

### Insertion Sort Implementation

Sorting uses **Insertion Sort algorithm** (classic algorithm, no lambdas):

```java
// Sort by customer name (ascending)
private void insertionSortByName(LinkedList<Account> accountList) {
    for (int i = 1; i < accountList.size(); i++) {
        Account currentAccount = accountList.get(i);
        String currentName = (currentAccount.getOwner() != null)
                ? currentAccount.getOwner().getName() : "";

        int j = i - 1;
        while (j >= 0) {
            Account compareAccount = accountList.get(j);
            String compareName = (compareAccount.getOwner() != null)
                    ? compareAccount.getOwner().getName() : "";

            if (currentName.compareToIgnoreCase(compareName) < 0) {
                accountList.set(j + 1, compareAccount);
                j--;
            } else {
                break;
            }
        }
        accountList.set(j + 1, currentAccount);
    }
}

// Sort by balance (descending)
private void insertionSortByBalance(LinkedList<Account> accountList) {
    for (int i = 1; i < accountList.size(); i++) {
        Account currentAccount = accountList.get(i);
        double currentBalance = currentAccount.getBalance();

        int j = i - 1;
        while (j >= 0) {
            Account compareAccount = accountList.get(j);
            double compareBalance = compareAccount.getBalance();

            if (currentBalance > compareBalance) {
                accountList.set(j + 1, compareAccount);
                j--;
            } else {
                break;
            }
        }
        accountList.set(j + 1, currentAccount);
    }
}
```

---

## How Data Structures Support Banking Operations

### CRUD Operations
- **CREATE**: Add customers/accounts to LinkedList
- **READ**: Search LinkedList for customer/account by ID
- **UPDATE**: Modify account overdraft/interest rate
- **DELETE**: Remove customer/account, cascade delete accounts

### Transaction Processing
- **Queue**: Hold transactions for batch processing
- **LinkedList**: Store transaction history for audit trail
- **Enum**: Ensure only valid transaction types

### Real-World Banking Scenarios

#### Scenario 1: Customer Deposits Money
```
1. User requests deposit → deposit() method
2. Create Transaction object (type=DEPOSIT)
3. Add to Queue (txQueue.offer())
4. User continues with other transactions
5. Later: bank.processTransactionQueue()
   - Poll transactions from queue (FIFO)
   - Process each in order received
   - Add to transaction history (LinkedList)
```

#### Scenario 2: Generate Transaction History
```
1. Customer asks for account history
2. Access LinkedList<Transaction> transactionHistory
3. Iterate through in insertion order (chronological)
4. Display all transactions with timestamps
5. Perfect audit trail!
```

#### Scenario 3: Sort Accounts by Owner
```
1. User selects "Sort by customer name"
2. displayAccountsBeforeSort() - shows current order
3. Collections.sort(accountList, Comparator)
4. displayAccountsAfterSort() - shows sorted order
5. Demonstrates sorting algorithm with custom Comparator
```

---

## Performance Characteristics

### Overall System Performance

| Operation | Data Structure | Complexity | Frequency |
|-----------|------------------|-----------|-----------|
| Add customer | LinkedList | O(1) | User creates customer |
| Find customer | LinkedList | O(n) | User looks up customer |
| Add transaction | Queue | O(1) | User requests transaction |
| Process all transactions | Queue | O(t) where t=transactions | End of batch |
| Display accounts | LinkedList | O(n) | User requests view |

### Why This is Good for Banking

1. **Adding transactions is fast** - O(1) encourages batch processing
2. **Processing in order** - Queue guarantees FIFO
3. **Searching is acceptable** - Banking systems have indexes in real world
4. **Audit trail is perfect** - LinkedList maintains chronological order

---

## 7. LinkedList<User> Password Change Operation (Phase 5 - NEW)

### Where Used
- **AuthenticationManager.java**:
  - `LinkedList<User> userRegistry` - Stores all authenticated users
  - `changePassword()` method uses `LinkedList.set()` operation

### Data Structure Operation: List Replacement

**The Challenge:**
When a user changes their password, the system must:
1. Find the old User object
2. Create a NEW User object with new password
3. Replace the old object with the new one in the LinkedList

**The Solution: LinkedList.set() Operation**

```java
public User changePassword(String username, String oldPassword, String newPassword) {
    // Find old user
    User currentUser = findUserByUsername(username);  // O(n) search

    if (!currentUser.authenticate(oldPassword)) {
        return null;  // Password mismatch
    }

    // Validate new password
    if (newPassword.length() < 4 || oldPassword.equals(newPassword)) {
        return null;  // Invalid password
    }

    // Create NEW User object with new password
    User newUser = new UserAccount(username, newPassword, linkedCustomerId);

    // Find index of old user in LinkedList
    int userIndex = userRegistry.indexOf(currentUser);  // O(n) search

    // CRITICAL OPERATION: Replace old with new in LinkedList
    userRegistry.set(userIndex, newUser);  // O(1) replacement

    // Log the change
    logAction(username, currentUser.getUserRole(), "CHANGE_PASSWORD",
              "User successfully changed their password");

    return newUser;  // Return new User for caller to update currentUser
}
```

### Time Complexity Analysis

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| findUserByUsername() | O(n) | Linear search through userRegistry |
| authenticate() | O(1) | String comparison |
| indexOf() | O(n) | Find index of user to replace |
| set() | O(1) | Direct replacement at index |
| **Total** | **O(n)** | Dominated by search operations |

### Why LinkedList.set() is Perfect for This Use Case

```
Old User Object (in LinkedList):
───────────────────────────────────
Index 0: Admin(username="admin")
Index 1: UserAccount(username="alice", password="alice123") ← Old password
Index 2: UserAccount(username="bob")
Index 3: UserAccount(username="charlie")

changePassword("alice", "alice123", "alice_new"):
  1. Find alice at index 1: O(n) search
  2. Create new UserAccount("alice", "alice_new", "C001")
  3. LinkedList.set(1, newUser): O(1) replacement

New User Object (in LinkedList):
───────────────────────────────────
Index 0: Admin(username="admin")
Index 1: UserAccount(username="alice", password="alice_new") ← New password!
Index 2: UserAccount(username="bob")
Index 3: UserAccount(username="charlie")
```

### Enterprise Design Pattern

This demonstrates the **Immutable Object Pattern** used in enterprise software:

```java
// Before: password field is final (cannot mutate)
public abstract class User {
    private final String password;  // IMMUTABLE
}

// Solution: Replace entire object instead of mutating password
// Old object → Garbage collected
// New object → Takes its place in registry
```

### Real-World Banking Scenario

```
Scenario: Customer Sarah changes password

Timeline:
─────────
1. Sarah logs in with temp password: "Welcomesa8743"
2. System checks: isPasswordChangeRequired() = true
3. Sarah selects: "21. Change Password"
4. Sarah enters: Current password = "Welcomesa8743" (verified)
5. Sarah enters: New password = "SecurePass2024"

Behind the scenes:
─────────────────
1. authManager.changePassword("sarah_chen", "Welcomesa8743", "SecurePass2024")
2. Find sarah_chen in userRegistry: userRegistry.indexOf(currentUser)
3. Create NEW UserAccount: new UserAccount("sarah_chen", "SecurePass2024", "C002")
4. Replace in registry: userRegistry.set(index, newUser)
5. Update currentUser: this.currentUser = newUser
6. Log action: "CHANGE_PASSWORD"

Result:
───────
Old User object discarded
New User object active with new password
Sarah stays logged in
Old password no longer works
```

### Integration with LinkedList<User> Registry

```java
// The LinkedList structure after password change:
LinkedList<User> userRegistry:
├─ [0] Admin("admin", "admin123")
├─ [1] UserAccount("sarah_chen", "SecurePass2024") ← REPLACED
├─ [2] UserAccount("alice", "alice123")
└─ [3] UserAccount("bob", "bob123")
```

### CC 204 Learning Outcome

This operation demonstrates:
- ✅ LinkedList as a mutable collection (can replace elements)
- ✅ Finding elements by searching: O(n) indexOf()
- ✅ Direct access replacement: O(1) set()
- ✅ Real-world use of LinkedList in authentication systems
- ✅ Immutable object pattern with mutable collections

---

## 7. Utility Classes (Supporting Data Structure Operations)

### Overview
Beyond core data structures, the Banking System uses utility classes to ensure consistent data access, validation, and operations across the codebase.

### ValidationPatterns.java
**Purpose:** Centralized validation rules and error messages

**Constants Provided:**
- Customer ID: `CUSTOMER_ID_PATTERN` (^C\d{3}$)
- Account Number: `ACCOUNT_NO_PATTERN` (^ACC\d{3}$)
- Profile ID: `PROFILE_ID_PATTERN` (^P\d{3}$)
- Email: `EMAIL_PATTERN` (standard email regex)
- Phone: `PHONE_MIN_DIGITS` (10 digits minimum)
- Transaction Status: `TRANSACTION_STATUS_PATTERN` (COMPLETED|FAILED)

**Methods:**
- `matchesPattern(String value, String pattern)` - Regex validation with null-safety
- `isValidPhoneNumber(String phone)` - Dedicated phone validation
- `isNonEmpty(String value)` - Trim-aware empty checking

**Why It's Needed:**
- ✅ Single source of truth for validation rules
- ✅ Consistent error messages across system
- ✅ Easy to update validation rules without code search
- ✅ DRY principle: Rules defined once, used everywhere

**Example Usage:**
```java
// Instead of repeating validation everywhere:
if (!customerIdInput.matches("^C\\d{3}$")) {
    // ...
}

// Use centralized validation:
if (!ValidationPatterns.matchesPattern(customerIdInput, ValidationPatterns.CUSTOMER_ID_PATTERN)) {
    System.out.println(ValidationPatterns.CUSTOMER_ID_ERROR);
}
```

### InputValidator.java
**Purpose:** User input collection with validation and cancellation support

**Key Methods:**
- `getValidatedString(String prompt)` - Validated string with "back" cancellation
- `getValidatedAmount(String prompt)` - Validated positive double
- `getValidatedInt(String prompt)` - Validated integer input
- `getValidatedInput(String prompt, String pattern, String hint)` - Pattern-based validation
- `getValidatedCustomer(...)` - Find and return validated customer
- `getValidatedAccount(...)` - Find and return validated account
- `confirmAction(String message)` - Yes/no confirmation with error handling
- `safeLogAction(BankingSystem bs, String action, String details)` - Null-safe audit logging

**Why It's Needed:**
- ✅ Consolidates repeated input logic (prevents code duplication)
- ✅ Consistent validation across all handlers
- ✅ Cancellation support ("back" keyword) uniformly implemented
- ✅ Error handling in one place
- ✅ Uses ValidationPatterns internally

**Data Structure Connection:**
```java
// InputValidator uses LinkedList<Customer> and LinkedList<Account> internally
// to provide methods like:
public Customer getValidatedCustomer() {
    // Searches LinkedList<Customer> to find and validate customer
    for (Customer c : customers) {
        if (c.getCustomerId().equals(input)) {
            return c;  // Found
        }
    }
    return null;  // Not found
}
```

### AccountUtils.java
**Purpose:** Account lookup operations

**Methods:**
- `findAccount(LinkedList<Account> accountList, String accountNo)` - Linear search for account

**Why It's Needed:**
- ✅ Centralizes account lookup logic
- ✅ Used in multiple places (InputValidator, managers, transaction processor)
- ✅ Single algorithm ensures consistency
- ✅ Easy to replace with database query later

**Usage Locations:**
```
- InputValidator.getValidatedAccount()
- CustomerManager cascading delete
- AccountManager findAccount()
- TransactionProcessor (deposit/withdraw/transfer)
```

### Summary: Utilities Support Data Structure Operations

| Utility | Supports | Purpose |
|---------|----------|---------|
| **ValidationPatterns** | All input fields | Centralize validation rules & error messages |
| **InputValidator** | LinkedList searching | Consistent input collection & validation |
| **AccountUtils** | LinkedList searching | Centralized account lookup |

**DRY Principle Achievement:**
- ✅ Validation rules defined once (ValidationPatterns)
- ✅ Input handling done once (InputValidator)
- ✅ Account lookup done once (AccountUtils)
- ✅ Safe logging done once (InputValidator.safeLogAction())
- ✅ Used consistently across all 3 manager classes

---

## Learning Outcomes (CC 204 - Data Structures & Algorithms)

This project demonstrates:

✅ **LinkedList**: Dynamic storage, insertion order preservation, sequential access
✅ **Queue**: FIFO processing, real-world applicability, asynchronous handling
✅ **Stack**: LIFO processing, display recent items first
✅ **Enum**: Type safety, compiler enforcement, categorical data
✅ **Insertion Sort**: Classic sorting algorithm, O(n²) complexity, in-place sorting
✅ **Time Complexity**: Understanding O(1), O(n), O(n²) operations
✅ **Algorithm Design**: FIFO vs LIFO, batch processing, transaction pipeline

---

## Conclusion

The data structures chosen are:
- **Appropriate** for banking domain (FIFO transactions, chronological history)
- **Efficient** for the operations needed (fast append, safe deletion)
- **Real-world relevant** (actual bank systems use similar patterns)
- **Well-justified** for both OOP and Data Structures rubrics
- **Non-tree-based** per rubric requirement (using LinkedList + Collections.sort instead)

Each structure serves a specific purpose and demonstrates proper software design principles.
