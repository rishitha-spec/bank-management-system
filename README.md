# Bank Management System

A web-based Bank Management System developed using Java, Spring Boot, Spring Security, Thymeleaf, JDBC, and MySQL.

## Features

- Login and logout authentication
- Create new customer accounts
- View all customer accounts
- Search customers by account number
- Deposit money with a maximum deposit limit of ₹50,000
- Withdraw money with balance validation
- View transaction history
- Delete eligible customer accounts
- Prevent deletion of accounts with transaction records
- MySQL database integration
- Input validation and error handling

## Technologies Used

- Java 21
- Spring Boot
- Spring Security
- Thymeleaf
- JDBC
- MySQL
- Maven
- HTML and CSS

## Requirements

- JDK 21
- MySQL Server
- Visual Studio Code or another Java IDE

## Database Setup

1. Start MySQL Server.
2. Create a database named `bankdb`.
3. Configure the database connection using these environment variables:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`

## Login Configuration

Set these environment variables before starting the application:

- `BANK_USERNAME`
- `BANK_PASSWORD`

The current version uses a predefined demonstration login stored in memory. Bank customers do not have separate login credentials.

## Run the Application

Open a terminal in the project directory.

Set the required environment variables and run:

    .\mvnw.cmd spring-boot:run

Open your browser and visit:

    http://localhost:8080

## Disclaimer

This project was developed for educational purposes and is not intended for real banking transactions or production use.