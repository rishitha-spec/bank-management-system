# Bank Management System

A Java-based Bank Management System that uses Spring Boot, JDBC, and MySQL to manage customer accounts and banking transactions. The project includes a console-based application alongside the existing web application.

## Project Overview

The application provides basic banking operations through a menu-driven console interface. It connects to a MySQL database to store customer information, account balances, and transaction history.

## Features

- **Login Authentication:** Login before accessing the console menu.
- **Create Account:** Register a customer with an account number, name, email, and account type.
- **View All Accounts:** Display registered customer accounts and their balances.
- **Search Account:** Find a customer using their account number.
- **Deposit Money:** Add money to an account and record the transaction.
- **Withdraw Money:** Withdraw money subject to the application's validation rules.
- **Transaction History:** View recorded deposits and withdrawals.
- **Delete Account:** Delete an account when the application's deletion rules permit it.
- **Database Integration:** Store and retrieve records using MySQL and JDBC.

## Technologies Used

- Java 21
- Spring Boot 4.0.8
- JDBC and Spring JDBC
- MySQL
- Maven
- Visual Studio Code
- Git and GitHub

## Project Structure

```text
bank-management-system/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/rishi/bank_management_system/
│       │       ├── BankManagementSystemApplication.java
│       │       ├── BankConsole.java
│       │       ├── Customer.java
│       │       ├── CustomerRepository.java
│       │       ├── CustomerController.java
│       │       ├── HomeController.java
│       │       ├── LoginController.java
│       │       ├── Transaction.java
│       │       ├── SecurityConfig.java
│       │       ├── InvalidAccount.java
│       │       ├── InsufficientFunds.java
│       │       └── InvalidAmt.java
│       └── resources/
│           ├── application.yaml
│           └── templates/
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## Requirements

Before running the project, install:

1. Java JDK 21 or a compatible supported version.
2. MySQL Server and MySQL Workbench.
3. Maven Wrapper, included in the project.
4. Git, if you want to clone the repository.

## Database Setup

1. Start MySQL Server.
2. Open MySQL Workbench and connect to your local server.
3. Create the database if it does not already exist:

```sql
CREATE DATABASE bankdb;
```

4. Ensure the database contains the required `customers` and `transactions` tables with the columns expected by `CustomerRepository.java`.
5. Configure your database connection using environment variables.

The application expects the following database settings:

```text
DB_URL=jdbc:mysql://localhost:3306/bankdb
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
```

Replace `your_mysql_password` with your own MySQL password locally. Never commit your actual password to GitHub.

## Running the Console Application

Open PowerShell in the project root directory.

### 1. Configure the database password

```powershell
$env:DB_PASSWORD = "your_mysql_password"
```

Replace the placeholder with your actual local MySQL password. Do not share it publicly.

### 2. Configure the console login

```powershell
$env:BANK_USERNAME = "bankuser"
$env:BANK_PASSWORD = "your_console_password"
```

Choose your own console password and use the same value whenever you log in during that terminal session.

### 3. Start the console application

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.main-class=com.rishi.bank_management_system.BankConsole"
```

### 4. Use the menu

```text
===== BANK MANAGEMENT SYSTEM =====
1. Create Account
2. View All Accounts
3. Search Account
4. Deposit Money
5. Withdraw Money
6. View Transactions
7. Delete Account
8. Exit
```

Enter the number corresponding to the operation you want to perform and follow the prompts.

**Note:** The environment variables above apply to the current PowerShell session. Set them again in a new terminal before running the application.

## Running the Existing Web Application

The repository also contains the existing Spring Boot web application, controllers, templates, and security configuration.

The console application and web application have different entry points and startup requirements. Follow the appropriate configuration for the version you want to run.

## Testing

The console application has been manually tested for:

- Login
- Account creation and listing
- Account search
- Deposits and withdrawals
- Transaction history display
- Account deletion handling
- MySQL connectivity

## Future Enhancements

- Add automated unit and integration tests.
- Improve input validation and error handling.
- Add transaction rollback and stronger financial-operation safeguards.
- Improve login security and password management.
- Add account statements and transaction filtering.
- Improve the web application's user interface.

## Author

**Rishitha Anisetti**

GitHub: [rishitha-spec](https://github.com/rishitha-spec)

## Repository

[Bank Management System on GitHub](https://github.com/rishitha-spec/bank-management-system)

## Disclaimer

This is an educational project developed to demonstrate Java programming, database connectivity, and basic banking operations. It is not intended for real banking or financial transactions.