# CLASS CONNECTIONS GUIDE - Banking System

## Complete Explanation of How Every Class Connects to Every Other Class

---

## TABLE OF CONTENTS
1. Types of Connections (Extends, Composition, etc.)
2. Class-by-Class Connection Details
3. Visual Connection Maps
4. Real-World Data Flow Examples
5. Quick Reference Table

---

## PART 1: TYPES OF CONNECTIONS

### Connection Type 1: INHERITANCE (`extends`)
**What it means:** One class IS-A special version of another class
**Symbol:** Triangle pointing up (▲)
**How it works:** Child class gets ALL methods and fields from parent, can override methods

#### Example: Account and SavingsAccount
```java
// Parent class (ABSTRACT)
public abstract class Account {
    protected double balance;
    public abstract boolean withdraw(double amount);
}

// Child class (CONCRETE)
public class SavingsAccount extends Account {
    private double interestRate;

    @Override
    public boolean withdraw(double amount) {
        // SavingsAccount's special rule: no overdraft allowed
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
```

**What happens:**
- `SavingsAccount` inherits `balance` from `Account`
- `SavingsAccount` implements its own `withdraw()` method
- When you call `withdraw()` on SavingsAccount, it uses SavingsAccount's version, not Account's

**Other Inheritance Examples in System:**
```
Account (parent)
  ├── SavingsAccount (child) - No overdraft, has interest
  └── CheckingAccount (child) - Has overdraft limit

User (parent)
  ├── Admin (child) - 21 permissions
  └── UserAccount (child) - 8 permissions, linked to customer
```

---

### Connection Type 2: COMPOSITION (`has-a`)
**What it means:** One class OWNS another class (strong ownership)
**Symbol:** Filled Diamond (◆)
**How it works:** Parent creates child in constructor and is responsible for its lifetime

#### Example: BankingSystem and CustomerManager
```java
public class BankingSystem {
    private CustomerManager customerMgr;  // ◆ COMPOSITION
    private AccountManager accountMgr;    // ◆ COMPOSITION

    public BankingSystem(Scanner scanner) {
        // BankingSystem CREATES and OWNS these managers
        this.customerMgr = new CustomerManager(customers, validator);
        this.accountMgr = new AccountManager(accountList, validator);
    }

    // When BankingSystem is destroyed, managers are destroyed too
}
```

**What happens:**
- BankingSystem creates CustomerManager in its constructor
- BankingSystem is responsible for CustomerManager's lifetime
- If BankingSystem is destroyed, CustomerManager is destroyed too
- **Important:** BankingSystem uses composition because it NEEDS these managers to exist

**Other Composition Examples:**
```
BankingSystem ◆─── InputValidator (needed for validation)
BankingSystem ◆─── CustomerManager (needed for customer CRUD)
BankingSystem ◆─── AccountManager (needed for account CRUD)
BankingSystem ◆─── TransactionProcessor (needed for transactions)
BankingSystem ◆─── AuthenticationManager (needed for auth)
```

---

### Connection Type 3: AGGREGATION (`has-a` but weaker)
**What it means:** One class CONTAINS another class (weak ownership)
**Symbol:** Circle (○)
**How it works:** Parent stores collection of objects but doesn't create/destroy them

#### Example: BankingSystem and Customer
```java
public class BankingSystem {
    private LinkedList<Customer> customers;  // ○ AGGREGATION

    public void addCustomer(Customer newCustomer) {
        customers.add(newCustomer);  // Store reference to customer
    }

    // Note: BankingSystem doesn't CREATE the customer object
    // It just stores a reference to it
}
```

**What happens:**
- BankingSystem stores LinkedList of Customers
- Each Customer can exist independently
- BankingSystem doesn't create Customers (CustomerManager does)
- **Important:** Different from COMPOSITION - the customers can outlive BankingSystem

**Other Aggregation Examples:**
```
BankingSystem ○─── LinkedList<Customer> (stores, doesn't create)
BankingSystem ○─── LinkedList<Account> (stores, doesn't create)
Customer ○─── LinkedList<Account> (stores customer's accounts)
Account ○─── LinkedList<Transaction> (stores account's transaction history)
AuthenticationManager ○─── LinkedList<User> (stores all users)
AuthenticationManager ○─── LinkedList<AuditLog> (stores audit records)
```

---

### Connection Type 4: ASSOCIATION (`uses` or `depends on`)
**What it means:** One class USES another class
**Symbol:** Arrow (→)
**How it works:** Class calls methods on another class, but doesn't own it

#### Example: TransactionProcessor uses Account
```java
public class TransactionProcessor {
    public void deposit(String accountNo, double amount) {
        // Find the account
        Account account = AccountUtils.findAccount(accountList, accountNo);

        // USE the account's method
        account.deposit(amount);  // → Calls deposit() on Account

        // Create and add transaction
        Transaction tx = new Transaction(...);
        account.addTransaction(tx);  // → Calls addTransaction() on Account
    }
}
```

**What happens:**
- TransactionProcessor doesn't own Account
- TransactionProcessor finds Account and calls its methods
- When TransactionProcessor is done, Account still exists
- **Important:** It's a "using" relationship, not ownership

**Other Association Examples:**
```
CustomerManager → Customer (creates and works with customers)
AccountManager → Account (creates and works with accounts)
TransactionProcessor → Account (finds and updates accounts)
InputValidator → ValidationPatterns (uses static validation methods)
InputValidator → AccountUtils (uses static utility methods)
```

---

## PART 2: CLASS-BY-CLASS CONNECTION DETAILS

### 1. Main Class
```
PURPOSE: Entry point to the application
CONNECTIONS:
  → BankingSystem (creates one instance)

HOW IT WORKS:
  public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      BankingSystem system = new BankingSystem(scanner);  // ← Creates BankingSystem
      system.startApplication();
  }
```

---

### 2. BankingSystem Class (Facade Pattern)
```
PURPOSE: Main controller/orchestrator - simplifies complex operations
CONNECTIONS:

COMPOSITION (Strong Ownership - Creates and Owns):
  ◆ InputValidator validator
  ◆ CustomerManager customerMgr
  ◆ AccountManager accountMgr
  ◆ TransactionProcessor txProcessor
  ◆ AuthenticationManager authManager

AGGREGATION (Weak Ownership - Stores):
  ○ LinkedList<Customer> customers
  ○ LinkedList<Account> accountList
  ○ User currentUser

ASSOCIATION (Uses/Depends On):
  → All managers for CRUD operations
  → BankingSystem.canAccessAccount() for security enforcement

WHY THESE CONNECTIONS:
  • Composition: Needs these managers to exist immediately
  • Aggregation: Stores business data (customers, accounts)
  • Association: Delegates operations to managers

EXAMPLE DATA FLOW:
  User selects "Create Customer"
    ↓
  BankingSystem.runConsoleMenu() receives option
    ↓
  Calls customerMgr.handleCreateCustomer()  (← delegates to manager)
    ↓
  Manager creates Customer object
    ↓
  Manager adds to customers list
```

---

### 3. CustomerManager Class
```
PURPOSE: Handles all customer CRUD operations
CONNECTIONS:

USES (Association):
  → InputValidator (for input validation and UI)
  → ValidationPatterns (inherited through InputValidator)
  → LinkedList<Customer> customers (from BankingSystem)
  → Customer (creates and modifies)
  → CustomerProfile (creates and modifies)

HOW IT CONNECTS:
  public void handleCreateCustomer() {
      // 1. Uses InputValidator to get input
      String name = validator.getCancellableInput("Enter customer name: ");

      // 2. Creates Customer object
      Customer customer = new Customer(autoGeneratedId, name);

      // 3. Adds to LinkedList
      customers.add(customer);
  }

WHY:
  • Uses InputValidator because validation is delegated
  • Works with Customer objects directly
  • Stores them in customers LinkedList
```

---

### 4. AccountManager Class
```
PURPOSE: Handles all account CRUD operations, sorting, interest
CONNECTIONS:

USES (Association):
  → InputValidator (for input validation)
  → LinkedList<Account> accountList (from BankingSystem)
  → Account (abstract class)
  → SavingsAccount (creates instances)
  → CheckingAccount (creates instances)
  → Customer (finds account owner)

HOW IT CONNECTS:
  public void handleCreateAccount() {
      // 1. Ask which type
      String type = validator.getCancellableInput("Checking or Savings?");

      // 2. Ask which customer
      Customer owner = findCustomer();

      // 3. Create EITHER SavingsAccount OR CheckingAccount
      if (type.equals("Savings")) {
          Account account = new SavingsAccount(...);  // ← Polymorphism!
      } else {
          Account account = new CheckingAccount(...);
      }

      // 4. Add to accountList
      accountList.add(account);
      owner.addAccount(account);  // Also add to customer's accounts
  }

KEY CONNECTION: POLYMORPHISM
  Account is abstract, but we create SavingsAccount OR CheckingAccount
  When we call account.withdraw() later, Java uses the RIGHT VERSION
```

---

### 5. TransactionProcessor Class
```
PURPOSE: Handles deposit, withdraw, transfer, history
CONNECTIONS:

USES (Association):
  → InputValidator (for input/output)
  → LinkedList<Account> accountList (finds accounts)
  → Account (calls deposit/withdraw methods)
  → Transaction (creates transaction records)
  → Stack<Transaction> (LIFO display of history)
  → BankingSystem.canAccessAccount() (security check)

HOW IT CONNECTS:
  public void handleDeposit() {
      // 1. Get account number
      String accountNo = validator.getCancellableInput("Account number: ");

      // 2. Find account using utility
      Account account = AccountUtils.findAccount(accountList, accountNo);

      // 3. Call account's deposit method
      account.deposit(amount);  // ← Calls the account's method

      // 4. Create transaction and add to account's history
      Transaction tx = new Transaction(account.getAccountNo(), ...);
      account.addTransaction(tx);  // ← Account stores transaction

      // 5. For history display: use Stack (LIFO)
      Stack<Transaction> recentTxns = new Stack<>();
      for (Transaction t : account.getTransactionHistory()) {
          recentTxns.push(t);  // Reverse order for LIFO
      }
  }

KEY: Each Account stores its own LinkedList<Transaction> history
```

---

### 6. AuthenticationManager Class
```
PURPOSE: Handles login, user registration, password change, audit logging
CONNECTIONS:

AGGREGATION (Weak Ownership - Stores):
  ○ LinkedList<User> userRegistry (all system users)
  ○ LinkedList<AuditLog> auditTrail (all actions logged)

USES (Association):
  → User (abstract class)
  → Admin (creates admin instances)
  → UserAccount (creates customer account instances)
  → AuditLog (creates immutable log entries)

HOW IT CONNECTS:
  public User login(String username, String password) {
      // 1. Search LinkedList<User> for matching username
      for (User user : userRegistry) {
          if (user.getUsername().equals(username)) {

              // 2. Verify password (polymorphic)
              if (user.authenticate(password)) {
                  // 3. Log the action (create AuditLog)
                  AuditLog log = new AuditLog(timestamp, username, user.getUserRole(),
                                              "LOGIN", "Successful login");
                  auditTrail.add(log);  // ← Store audit record

                  return user;  // User IS-A Admin or UserAccount
              }
          }
      }
      return null;
  }

KEY RELATIONSHIPS:
  User (abstract)
    ├── Admin extends User ← Different getPermissions() (21 perms)
    └── UserAccount extends User ← Different getPermissions() (8 perms)
```

---

### 7. User Class (Abstract)
```
PURPOSE: Define common user properties and behavior
INHERITANCE TREE:

  User (abstract)
    ├── extends Admin
    └── extends UserAccount

HOW INHERITANCE WORKS:

// Parent class (ABSTRACT)
public abstract class User {
    private final String username;  // All users have username
    private final String password;  // All users have password
    private final UserRole userRole;  // All users have a role

    public abstract LinkedList<String> getPermissions();  // Must override
}

// Child class 1
public class Admin extends User {
    @Override  // Override parent's abstract method
    public LinkedList<String> getPermissions() {
        LinkedList<String> perms = new LinkedList<>();
        perms.add("CREATE_CUSTOMER");
        perms.add("DELETE_CUSTOMER");
        // ... 19 more permissions
        return perms;  // Admin has 21 permissions
    }
}

// Child class 2
public class UserAccount extends User {
    private final String linkedCustomerId;  // Extra field for UserAccount

    @Override  // Override parent's abstract method
    public LinkedList<String> getPermissions() {
        LinkedList<String> perms = new LinkedList<>();
        perms.add("DEPOSIT");
        perms.add("WITHDRAW");
        perms.add("VIEW_HISTORY");
        // ... 5 more permissions
        return perms;  // Customer has 8 permissions
    }
}

WHY THIS DESIGN:
  ✓ Code reuse: Common fields (username, password) in parent
  ✓ Polymorphism: Same method name, different behavior
  ✓ Type safety: Both Admin and UserAccount are User subclasses
  ✓ Extensibility: Easy to add new user types
```

---

### 8. Account Class (Abstract)
```
PURPOSE: Define common account properties and behavior
INHERITANCE TREE:

  Account (abstract)
    ├── extends SavingsAccount
    └── extends CheckingAccount

HOW INHERITANCE WORKS:

// Parent class (ABSTRACT)
public abstract class Account {
    protected String accountNo;
    protected double balance;
    protected Customer owner;
    protected LinkedList<Transaction> transactionHistory;  // All accounts have history

    public void deposit(double amount) {
        // Common deposit logic for all accounts
        balance += amount;
        // ... create transaction, etc
    }

    public abstract boolean withdraw(double amount);  // Each account type decides
    public abstract String getDetails();  // Each account displays differently
}

// Child class 1
public class SavingsAccount extends Account {
    private double interestRate;

    @Override
    public boolean withdraw(double amount) {
        // Savings rule: NO overdraft allowed
        if (balance >= amount) {  // Strict rule
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String getDetails() {
        return "Savings Account " + accountNo +
               ", Balance: $" + balance +
               ", Interest Rate: " + (interestRate*100) + "%";
    }

    public void applyInterest() {
        balance = balance * (1 + interestRate);  // Add interest
    }
}

// Child class 2
public class CheckingAccount extends Account {
    private double overdraftLimit;

    @Override
    public boolean withdraw(double amount) {
        // Checking rule: CAN use overdraft
        if (balance + overdraftLimit >= amount) {  // Relaxed rule
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String getDetails() {
        return "Checking Account " + accountNo +
               ", Balance: $" + balance +
               ", Overdraft Limit: $" + overdraftLimit;
    }
}

WHY THIS DESIGN:
  ✓ deposit() is same for all accounts (in parent)
  ✓ withdraw() is DIFFERENT for each account (override in children)
  ✓ getDetails() shows different info per account type
  ✓ Polymorphism: You can write: Account acc = new SavingsAccount(...)
                  acc.withdraw(50) uses SavingsAccount's withdraw(), not Account's
```

---

### 9. Customer Class
```
PURPOSE: Represent a bank customer
CONNECTIONS:

AGGREGATION (Stores):
  ○ LinkedList<Account> accounts (customer's accounts)
  ↔ CustomerProfile profile (1:1 relationship)

HOW IT CONNECTS:
  public class Customer {
      private String customerId;  // Auto-generated: C001, C002, etc.
      private String name;
      private LinkedList<Account> accounts;  // Can have many accounts
      private CustomerProfile profile;  // Has exactly one profile

      public void addAccount(Account account) {
          accounts.add(account);  // ← Store account
      }

      public LinkedList<Account> getAccounts() {
          return accounts;  // ← Retrieve all accounts
      }
  }

RELATIONSHIP WITH ACCOUNT:
  One Customer → Many Accounts

  Customer (1) ──────→ (Many) Account

  Example:
    Customer: Juan Rodriguez (C001)
      ├── Account: ACC001 (Checking) - Balance: $5000
      ├── Account: ACC002 (Savings) - Balance: $10000
      └── Account: ACC003 (Savings) - Balance: $2000
```

---

### 10. Account and Its Connection to Transaction
```
PURPOSE: Account stores transaction history
CONNECTIONS:

AGGREGATION (Weak Ownership):
  ○ LinkedList<Transaction> transactionHistory

HOW TRANSACTIONS ARE STORED:
  public class Account {
      protected LinkedList<Transaction> transactionHistory;

      public void addTransaction(Transaction tx) {
          transactionHistory.add(tx);  // ← Every action creates a transaction
      }

      public LinkedList<Transaction> getTransactionHistory() {
          return transactionHistory;
      }
  }

TRANSACTION LIFECYCLE:
  1. User deposits $100
  2. Account.deposit(100) called
  3. Balance updated
  4. Transaction object created:
     Transaction tx = new Transaction("ACC001", "ACC001", DEPOSIT, 100, NOW, "COMPLETED")
  5. tx added to account.transactionHistory
  6. LinkedList grows: [tx1, tx2, tx3, tx4, ...]
  7. TransactionProcessor can display using Stack (LIFO - most recent first)

EXAMPLE:
  Account ACC001 history (in LinkedList order):
    [0] TX001: DEPOSIT $100 at 2025-11-18 10:00:00
    [1] TX002: WITHDRAW $50 at 2025-11-18 10:15:00
    [2] TX003: TRANSFER $200 to ACC002 at 2025-11-18 10:30:00

  When displayed with Stack (LIFO):
    TX003: TRANSFER $200 ← Most recent (shown first)
    TX002: WITHDRAW $50
    TX001: DEPOSIT $100 ← Oldest (shown last)
```

---

### 11. InputValidator and ValidationPatterns Connection
```
PURPOSE: Validate all user input, use centralized patterns
CONNECTIONS:

ASSOCIATION:
  InputValidator → ValidationPatterns (uses static methods)

HOW THEY CONNECT:
  public class InputValidator {
      public String getValidatedName(String prompt) {
          String input = getCancellableInput(prompt);

          // Use ValidationPatterns to check
          if (ValidationPatterns.isNonEmpty(input)) {
              return input;
          }
          System.out.println(ValidationPatterns.INVALID_NAME_ERROR);
          return null;
      }
  }

  public class ValidationPatterns {
      public static final String CUSTOMER_ID_PATTERN = "^C\\d{3}$";  // C001, C002, etc.
      public static final String ACCOUNT_NO_PATTERN = "^ACC\\d{3}$";  // ACC001, etc.

      public static boolean matchesPattern(String input, String pattern) {
          return input.matches(pattern);  // ← Regex check
      }

      public static boolean isValidPhoneNumber(String phone) {
          return phone.replaceAll("\\D", "").length() >= 10;
      }
  }

WHY THIS CONNECTION:
  ✓ DRY Principle: All patterns in one place
  ✓ Reusability: Multiple classes use same patterns
  ✓ Maintainability: Change pattern once, affects everywhere
```

---

### 12. AuthenticationManager and AuditLog Connection
```
PURPOSE: Record all actions for security/compliance
CONNECTIONS:

AGGREGATION (Weak Ownership):
  ○ LinkedList<AuditLog> auditTrail

HOW THEY CONNECT:
  public class AuthenticationManager {
      private LinkedList<AuditLog> auditTrail;

      public void logAction(String username, String action, String details) {
          // Create new immutable AuditLog
          AuditLog log = new AuditLog(LocalDateTime.now(), username,
                                      userRole, action, details);

          // Store in LinkedList
          auditTrail.add(log);  // ← Chronological record
      }
  }

AUDIT TRAIL EXAMPLE:
  [0] 2025-11-18 09:00:00 | diana_prince | ADMIN | LOGIN | Successful login
  [1] 2025-11-18 09:05:00 | diana_prince | ADMIN | CREATE_CUSTOMER | Created C001
  [2] 2025-11-18 09:10:00 | diana_prince | ADMIN | CREATE_ACCOUNT | Created ACC001
  [3] 2025-11-18 09:15:00 | john_doe | CUSTOMER | DEPOSIT | Deposited $100
  [4] 2025-11-18 09:20:00 | john_doe | CUSTOMER | CHANGE_PASSWORD | Password changed
  [5] 2025-11-18 09:25:00 | diana_prince | ADMIN | VIEW_AUDIT_TRAIL | Viewed audit log

WHY LINKEDLIST:
  ✓ Maintains chronological order (insertion order)
  ✓ Immutable AuditLog objects can't be modified
  ✓ Complete history for compliance
```

---

## PART 3: VISUAL CONNECTION MAPS

### Connection Map 1: Inheritance Connections

```
User (Abstract Parent)
  │
  ├─ Abstract Methods:
  │  └─ getPermissions() ◆ (child must override)
  │
  ├─ Common Fields:
  │  ├─ username (final)
  │  ├─ password (final)
  │  └─ userRole
  │
  └─ extends ────┬──────┬────────┐
                 │      │        │
                 ▼      ▼        ▼
              Admin  UserAccount (also extends User)
              (concrete) (concrete)

              Admin extends User
                ✓ Inherits: username, password, userRole
                ✓ Overrides: getPermissions() → returns 21 admin permissions
                ✓ New: (no new fields)

              UserAccount extends User
                ✓ Inherits: username, password, userRole
                ✓ Overrides: getPermissions() → returns 8 customer permissions
                ✓ New: linkedCustomerId (links to specific customer)


Account (Abstract Parent)
  │
  ├─ Abstract Methods:
  │  ├─ withdraw() ◆
  │  └─ getDetails() ◆
  │
  ├─ Common Fields:
  │  ├─ accountNo
  │  ├─ balance
  │  ├─ owner (Customer)
  │  └─ transactionHistory (LinkedList<Transaction>)
  │
  ├─ Common Methods:
  │  └─ deposit(amount) ← SAME for all account types
  │
  └─ extends ────┬──────────┬────────────┐
                 │          │            │
                 ▼          ▼            ▼
            SavingsAccount CheckingAccount
            (concrete)     (concrete)

            SavingsAccount extends Account
              ✓ Inherits: All fields and deposit()
              ✓ Overrides: withdraw() → NO overdraft allowed
              ✓ Overrides: getDetails() → Shows interest rate
              ✓ New: interestRate, applyInterest()

            CheckingAccount extends Account
              ✓ Inherits: All fields and deposit()
              ✓ Overrides: withdraw() → CAN use overdraft
              ✓ Overrides: getDetails() → Shows overdraft limit
              ✓ New: overdraftLimit, setOverdraftLimit()
```

---

### Connection Map 2: Composition & Aggregation

```
BankingSystem (Facade)
  │
  ├─◆ COMPOSITION (Strong Ownership - BankingSystem CREATES these)
  │  ├─ InputValidator validator
  │  │  └─ Manages: ValidationPatterns, AccountUtils
  │  │
  │  ├─ CustomerManager customerMgr
  │  │  └─ Uses: InputValidator, customers LinkedList
  │  │
  │  ├─ AccountManager accountMgr
  │  │  └─ Uses: InputValidator, accountList LinkedList
  │  │
  │  ├─ TransactionProcessor txProcessor
  │  │  └─ Uses: InputValidator, Account, Stack, AuditLog
  │  │
  │  └─ AuthenticationManager authManager
  │     └─ Uses: User, AuditLog
  │
  └─○ AGGREGATION (Weak Ownership - BankingSystem STORES these)
     ├─ LinkedList<Customer> customers
     │  └─ Each Customer contains:
     │     ├─ LinkedList<Account> accounts
     │     │  └─ Each Account contains:
     │     │     └─ LinkedList<Transaction> history
     │     │
     │     └─ CustomerProfile profile (1:1)
     │
     └─ LinkedList<Account> accountList (separate master list)

        AuthenticationManager ALSO contains:
        ├─ LinkedList<User> userRegistry
        │  └─ Contains Admin and UserAccount objects
        │
        └─ LinkedList<AuditLog> auditTrail
           └─ Immutable log entries
```

---

### Connection Map 3: Association Chain (How Data Flows)

```
DEPOSIT FLOW:
User → BankingSystem.handleDeposit()
        → TransactionProcessor.handleDeposit()
           → InputValidator.getAccountNumber()  [gets input]
           → AccountUtils.findAccount()  [searches accountList]
           → Account.deposit(amount)  [calls method]
           → Transaction constructor  [creates record]
           → Account.addTransaction()  [stores in history]
           → InputValidator.safeLogAction()  [creates audit log]
           → AuthenticationManager.logAction()  [stores in auditTrail]


CREATE CUSTOMER FLOW:
User → BankingSystem.runConsoleMenu()
        → CustomerManager.handleCreateCustomer()
           → InputValidator.getValidatedInput()  [gets customer name]
           → Customer constructor  [creates new customer]
           → Auto-generate: customerID, username, password
           → AuthenticationManager.registerUser()  [create User object]
           → LinkedList<User> userRegistry.add()  [store user]
           → customers.add(newCustomer)  [store customer]
           → InputValidator.safeLogAction()  [log the action]
           → AuditLog created and stored


AUTHENTICATE USER FLOW:
User → AuthenticationManager.login(username, password)
        → Search LinkedList<User> userRegistry
        → User.authenticate(password)  [check password]
        → Find User (IS-A Admin or IS-A UserAccount)
        → Create AuditLog  [log the login]
        → Return User object
```

---

## PART 4: REAL-WORLD SCENARIO - Complete Data Flow

### Scenario: Customer Deposits $500

```
STEP-BY-STEP EXECUTION:

1. USER SELECTS DEPOSIT FROM MENU
   BankingSystem.runConsoleMenu() sees user selected "Deposit"

2. NAVIGATE TO TRANSACTION PROCESSOR
   BankingSystem delegates to: transactionProcessor.handleDeposit()

3. GET ACCOUNT DETAILS (uses InputValidator)
   InputValidator.getCancellableInput("Enter account number: ")
   → User enters: "ACC001"

4. FIND ACCOUNT (uses AccountUtils)
   AccountUtils.findAccount(accountList, "ACC001")
   → Searches LinkedList<Account> accountList
   → Finds: Account object with accountNo = "ACC001"
   → Returns: SavingsAccount (or CheckingAccount) object

5. GET AMOUNT (uses InputValidator)
   InputValidator.getDoubleInput("Enter amount: ")
   → User enters: 500.0

6. CHECK ACCESS CONTROL (security enforcement)
   BankingSystem.canAccessAccount(customerId, "ACC001")
   → Verifies: Current user owns this account
   → Returns: true (allowed) or false (blocked)

7. PROCESS DEPOSIT (calls Account method)
   account.deposit(500.0)

   Inside SavingsAccount.deposit():
     balance = balance + 500.0;  ← Balance updated
     // inherited from Account

8. CREATE TRANSACTION RECORD (persistence)
   Transaction tx = new Transaction(
       "TX##", "ACC001", "ACC001", DEPOSIT, 500.0, NOW, "COMPLETED"
   )

9. STORE TRANSACTION IN ACCOUNT (aggregation)
   account.addTransaction(tx)
   → account.transactionHistory.add(tx)  ← Stored in LinkedList

10. LOG ACTION (audit trail)
    InputValidator.safeLogAction(
        currentUser.getUsername(),
        "DEPOSIT",
        "Deposited $500 into ACC001"
    )
    → Creates AuditLog object
    → authManager.logAction() adds to auditTrail

11. DISPLAY CONFIRMATION (UI)
    System.out.println("Deposit successful!");
    System.out.println("New balance: $" + account.getBalance());

END RESULT:
  ✓ Account balance increased by $500
  ✓ Transaction recorded in account.transactionHistory
  ✓ Action logged in authManager.auditTrail
  ✓ All connections used:
    - BankingSystem (orchestrator)
    - TransactionProcessor (handler)
    - InputValidator (input/validation)
    - AccountUtils (finder)
    - Account (model)
    - Transaction (record)
    - AuthenticationManager (logger)
    - AuditLog (audit record)
```

---

## PART 5: QUICK REFERENCE TABLE

| Class | Type | Main Connections | Connection Type | Purpose |
|---|---|---|---|---|
| **Main** | Entry | BankingSystem | → | Start application |
| **BankingSystem** | Facade | All managers, collections | ◆ ○ | Orchestrate operations |
| **InputValidator** | Utility | ValidationPatterns, AccountUtils | → | Centralize validation |
| **ValidationPatterns** | Static Utility | (none) | - | Define validation rules |
| **AccountUtils** | Static Utility | Account | → | Find accounts |
| **CustomerManager** | Service | InputValidator, Customer, Profile | → | Manage customers |
| **AccountManager** | Service | InputValidator, Account, Savings, Checking | → | Manage accounts |
| **TransactionProcessor** | Service | InputValidator, Account, Transaction, Stack, AuthMgr | → | Manage transactions |
| **AuthenticationManager** | Service | User, Admin, UserAccount, AuditLog | ○ | Manage authentication |
| **Customer** | Model | Account, Profile | ○ | Represent customer |
| **CustomerProfile** | Model | Customer | ↔ | Extended customer info |
| **Account (Abstract)** | Model | Customer, Transaction | - | Base account |
| **SavingsAccount** | Model | Account ▲ | ▲ | Savings account |
| **CheckingAccount** | Model | Account ▲ | ▲ | Checking account |
| **Transaction** | Record | TransactionType | → | Transaction history |
| **User (Abstract)** | Model | Admin, UserAccount | - | Base user |
| **Admin** | Model | User ▲ | ▲ | Admin user |
| **UserAccount** | Model | User ▲, Customer | ▲ | Customer user |
| **AuditLog** | Immutable | (none) | - | Audit record |
| **TransactionType** | Enum | Transaction | → | Type safety |
| **UserRole** | Enum | User | → | Type safety |
| **MenuAction** | Enum | BankingSystem | → | Type safety |

---

## KEY TAKEAWAYS

### Connection Types Summary:
- **`extends` (Inheritance ▲):** IS-A relationship (Account IS-A SavingsAccount)
- **`◆` (Composition):** Strong ownership (BankingSystem CREATES managers)
- **`○` (Aggregation):** Weak ownership (BankingSystem STORES customers)
- **`→` (Association):** Uses/depends on (TransactionProcessor USES Account)
- **`↔` (Bidirectional):** Both directions (Customer ↔ Profile)

### Design Principles:
1. **Single Responsibility:** Each class has ONE job
2. **DRY (Don't Repeat Yourself):** Validation patterns centralized
3. **Polymorphism:** Different account types, same interface
4. **Immutability:** AuditLog records can't be changed
5. **Access Control:** canAccessAccount() enforces security

### Why This Architecture Works:
- **Easy to Maintain:** Changes to one class don't break others
- **Easy to Test:** Each class can be tested independently
- **Easy to Extend:** Add new account types without changing existing code
- **Professional:** Follows OOP best practices
- **Secure:** Access control and audit trails built-in

---

**Document Version:** 1.0
**Last Updated:** November 18, 2025
**Status:** Complete Explanation Ready for Presentation
