# Code Design Patterns & Encapsulation Strategy

## Overview

This document explains the OOP design patterns and encapsulation strategies used throughout the Banking Management System. Understanding these patterns is crucial for maintaining code quality and making future enhancements.

---

## 1. JavaBean Pattern (Model Classes)

### What It Is
The JavaBean pattern provides complete getter/setter pairs for all mutable private fields in model classes.

### Where It's Used
- **Customer.java** - All fields have getters and setters
- **CustomerProfile.java** - All fields have getters and setters
- **Account.java** - All mutable fields have getters and setters
- **SavingsAccount.java** - Interest rate has getter/setter
- **CheckingAccount.java** - Overdraft limit has getter/setter
- **Transaction.java** - All fields have getters and setters

### Why We Use It
1. **Consistency** - All data fields are accessible via uniform API
2. **Validation** - Setters can validate data before assignment
3. **Flexibility** - Can add logic later (logging, side effects) without breaking code
4. **Future-Proofing** - If external code needs these fields later, API already exists

### Example
```java
// JavaBean pattern in Customer.java
private String customerId;
private String name;

// Getters
public String getCustomerId() { return this.customerId; }
public String getName() { return this.name; }

// Setters with validation
public void setCustomerId(String customerId) {
    if (!ValidationPatterns.matchesPattern(customerId, ValidationPatterns.CUSTOMER_ID_PATTERN)) {
        throw new IllegalArgumentException(ValidationPatterns.CUSTOMER_ID_ERROR);
    }
    this.customerId = customerId;
}
```

---

## 2. Service Layer Pattern (Manager Classes)

### What It Is
Manager classes do NOT expose their internal data structures. Instead, they provide service methods for all operations.

### Where It's Used
- **CustomerManager.java** - 0 getters; provides `findCustomer()`, `createCustomer()`, `deleteCustomer()` methods
- **AccountManager.java** - 0 getters; provides `findAccount()`, `createAccount()`, `deleteAccount()` methods
- **TransactionProcessor.java** - 0 getters; provides `deposit()`, `withdraw()`, `transfer()` methods

### Why We Use It
1. **Encapsulation** - Prevents external code from directly manipulating data
2. **Business Logic Control** - All operations go through proper methods with validation
3. **DRY Principle** - Logic lives in one place, not scattered across codebase
4. **Audit Trail** - Can log all operations that modify data
5. **Security** - Prevents bypass of authorization checks

### Anti-Pattern Example (What We DON'T Do)
```java
// ❌ BAD - Direct collection access
LinkedList<Account> accounts = accountMgr.getAccountList();
accounts.clear();  // Deletes all accounts without validation or logging!

// ✅ GOOD - Service method
accountMgr.deleteAccount(accountNo);  // Proper validation, logging, authorization
```

### Example - Proper Service Layer
```java
// GOOD - AccountManager provides service methods, not data access
public class AccountManager {
    private LinkedList<Account> accountList;  // Private - no getter!

    // Service methods for external use
    public Account findAccount(String accountNo) { ... }
    public Account createAccount(Customer customer, String type) { ... }
    public boolean deleteAccount(String accountNo) { ... }
}
```

---

## 3. Facade Pattern (BankingSystem)

### What It Is
The Facade pattern selectively exposes only the components that external code needs to interact with.

### Where It's Used
**BankingSystem.java** - Exposes 2 out of 9 private fields:
- ✅ `getAuthenticationManager()` - Needed for auth operations
- ✅ `getCurrentUser()` - Needed for permission checks
- ❌ `customerMgr` - Hidden (access through service methods)
- ❌ `accountMgr` - Hidden (access through service methods)
- ❌ `accounts` - Hidden (access through service methods)

### Why We Use It
1. **Controlled Access** - Prevents bypass of business logic
2. **Simplicity** - Clients use simple methods, not juggling multiple objects
3. **Change Management** - Can change internal structure without affecting clients
4. **Security** - Can't access internal components directly

### Example
```java
// GOOD - BankingSystem exposes simple interface
public class BankingSystem {
    private CustomerManager customerMgr;  // Hidden
    private AccountManager accountMgr;    // Hidden

    // Exposed - needed for external use
    public User getCurrentUser() { return this.currentUser; }
    public AuthenticationManager getAuthenticationManager() { return this.authManager; }

    // Service methods instead of exposing managers
    public void createCustomer() { ... }  // Delegates to customerMgr
    public void deleteAccount() { ... }   // Delegates to accountMgr
}
```

---

## 4. Immutable Pattern (Security Classes)

### What It Is
Immutable classes have no setters; all fields are final and set at construction time.

### Where It's Used
- **AuditLog.java** - All 5 fields are final, no setters
- **User.java** - Partial (most fields final, one mutable for password changes)
- **UserAccount.java** - Inherits immutability from User

### Why We Use It
1. **Security** - Can't modify credentials after creation
2. **Thread Safety** - No synchronization needed
3. **Audit Integrity** - Audit logs can't be modified after recording
4. **Predictability** - Objects behave consistently throughout lifetime

### Example - AuditLog
```java
// AuditLog - Fully Immutable
public class AuditLog {
    private final LocalDateTime timestamp;  // Can't change
    private final String username;          // Can't change
    private final String action;            // Can't change

    // No setters - object is immutable
    // Only getters to read data
}
```

### Example - User
```java
// User - Selective Immutability
public class User {
    private final String username;          // Never changes
    private final String password;          // Final field, no getter
    private final UserRole userRole;        // Never changes
    private boolean passwordChangeRequired; // Mutable - for password workflow
}
```

---

## 5. Getter/Setter Decision Matrix

### When to Provide Getters

| Scenario | Provide Getter? | Example | Reason |
|----------|--|---|---|
| Model class private field | ✅ YES | Customer.customerId | JavaBean pattern - expected API |
| Collection in manager | ❌ NO | AccountManager.accountList | Service layer - use service methods instead |
| Security credential | ❌ NO | User.password | Security - never expose passwords |
| ID in immutable class | ✅ YES | AuditLog.username | Need to read immutable data |
| Status/state in transaction | ✅ YES | Transaction.status | Legitimate external query |
| Account in transaction | ✅ YES | Transaction.getFromAccountNo() | Needed for audit/reconciliation |

### Removed Getters (Why They Were Wrong)

#### getAccountList() - WRONG ❌
```java
// ❌ BAD - Violates Service Layer pattern
public LinkedList<Account> getAccountList() {
    return this.accountList;
}
// Problem: Exposes internal data structure
// Solution: Use service methods like findAccount(), createAccount()
```

#### getUserRegistry() - WRONG ❌
```java
// ❌ BAD - Violates encapsulation
public LinkedList<User> getUserRegistry() {
    return this.userRegistry;
}
// Problem: Could bypass authentication checks
// Solution: Use AuthenticationManager methods like findUser(), registerUser()
```

#### getAuditTrail() - WRONG ❌
```java
// ❌ BAD - Redundant with service method
public LinkedList<AuditLog> getAuditTrail() {
    return this.auditTrail;
}
// Problem: Already have displayAuditTrail() method for this
// Solution: Use displayAuditTrail() instead
```

### Kept Getters (Why They're Right)

#### getFromAccountNo() & getToAccountNo() - CORRECT ✅
```java
// ✅ GOOD - JavaBean pattern for model class
public String getFromAccountNo() { return this.fromAccountNo; }
public String getToAccountNo() { return this.toAccountNo; }
// Reason: Transaction is a model class
// Used for: Audit queries, reconciliation, future features
// Pattern: All Transaction fields have getters/setters
```

---

## 6. Design Pattern Summary Table

| Pattern | Used In | Purpose | Example |
|---------|---------|---------|---------|
| **JavaBean** | Model classes | Complete getter/setter API | Customer, Account, Transaction |
| **Service Layer** | Manager classes | Control data access via methods | CustomerManager, AccountManager |
| **Facade** | BankingSystem | Selective component exposure | getCurrentUser(), getAuthManager() |
| **Immutable** | Security classes | Prevent modification | AuditLog, User credentials |
| **Inheritance** | Account hierarchy | Code reuse, polymorphism | SavingsAccount, CheckingAccount extend Account |
| **Composition** | Relationships | "Has-A" rather than "Is-A" | Customer has-a CustomerProfile |
| **Dependency Injection** | Managers | Loose coupling | Pass InputValidator to managers |

---

## 7. Code Quality Principles Applied

### DRY Principle (Don't Repeat Yourself)
**Definition:** Write code once, use everywhere
**Examples in Our Code:**
- `InputValidator.safeLogAction()` - Consolidates 15+ null-check patterns
- `ValidationPatterns.CUSTOMER_ID_PATTERN` - Single source of truth for customer ID validation
- `AccountUtils.findAccount()` - Single account lookup method used in 4 places

### Single Responsibility Principle
**Definition:** Each class should have one reason to change
**Examples:**
- `ValidationPatterns` - Only responsible for validation rules
- `InputValidator` - Only responsible for user input
- `AccountManager` - Only responsible for account operations

### Open/Closed Principle
**Definition:** Open for extension, closed for modification
**Examples:**
- `Account` abstract class allows new account types (SavingsAccount, CheckingAccount) without modifying existing code

### Interface Segregation
**Definition:** Many client-specific interfaces rather than one general-purpose interface
**Examples:**
- InputValidator provides many specific methods (`getValidatedString()`, `getValidatedAmount()`, etc.)
- Not one generic `getInput()` method

---

## 8. Encapsulation Best Practices

### ✅ DO
- ✅ Make all fields private
- ✅ Provide getters for legitimate external needs
- ✅ Validate data in setters
- ✅ Use service methods to control data access
- ✅ Keep internal structures hidden
- ✅ Document WHY fields do/don't have getters

### ❌ DON'T
- ❌ Expose internal collections (use service methods instead)
- ❌ Create getters for fields that shouldn't be accessed
- ❌ Allow direct modification of sensitive data
- ❌ Return mutable references to internal state
- ❌ Bypass business logic with direct field access

---

## 9. Future Considerations

### If Adding GUI
Models (Customer, Account) can be used as-is - they already provide proper JavaBean interface.
Managers should return data instead of printing - already well-separated.

### If Adding Database
Service layer pattern makes this easy - just replace LinkedList operations with database calls.
Validation logic in model setters is reusable with any persistence layer.

### If Adding More Features
Existing patterns provide clear extension points:
- Add new account types by extending Account class
- Add new transaction types via TransactionType enum
- Add new validations in ValidationPatterns constants

---

## Conclusion

The Banking Management System uses industry-standard design patterns to ensure:
- ✅ Clean, maintainable code
- ✅ Proper encapsulation
- ✅ Reusable components
- ✅ Easy to test
- ✅ Easy to extend

These patterns are not just theoretical - they solve real problems and make the code professional and maintainable.

---

**Last Updated:** November 18, 2025
**Related Files:**
- README.md - General project overview
- DATA_STRUCTURES.md - Data structure usage
- CLAUDE.md - Implementation requirements
- UML_CLASS_DIAGRAM.md - Complete UML diagrams (PlantUML + ASCII) showing all 22 classes
- CLASS_CONNECTIONS_GUIDE.md - Detailed explanation of how all classes connect to each other
