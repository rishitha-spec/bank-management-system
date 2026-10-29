
package com.rishi.bank_management_system;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

public class BankConsole {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (ConfigurableApplicationContext context =
                new SpringApplicationBuilder(BankManagementSystemApplication.class)
                        .web(WebApplicationType.NONE)
                        .run(args)) {

            CustomerRepository repository =
                    context.getBean(CustomerRepository.class);

            if (!login()) {
                System.out.println("Invalid login. Exiting application.");
                return;
            }

            boolean running = true;

            while (running) {
                System.out.println("\n================================");
                System.out.println("     BANK MANAGEMENT SYSTEM");
                System.out.println("================================");
                System.out.println("1. Create Account");
                System.out.println("2. View All Accounts");
                System.out.println("3. Search Account");
                System.out.println("4. Deposit Money");
                System.out.println("5. Withdraw Money");
                System.out.println("6. View Transactions");
                System.out.println("7. Delete Account");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");

                String choice = sc.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> createAccount(repository);
                        case "2" -> viewAccounts(repository);
                        case "3" -> searchAccount(repository);
                        case "4" -> deposit(repository);
                        case "5" -> withdraw(repository);
                        case "6" -> viewTransactions(repository);
                        case "7" -> deleteAccount(repository);
                        case "8" -> {
                            running = false;
                            System.out.println("Thank you for using our bank!");
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (Exception e) {
                    System.out.println("Operation failed: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Application could not start.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean login() {
        String username = System.getenv("BANK_USERNAME");
        String password = System.getenv("BANK_PASSWORD");

        if (username == null || password == null) {
            System.out.println("Login credentials are not configured.");
            System.out.println("Set BANK_USERNAME and BANK_PASSWORD first.");
            return false;
        }

        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Enter username: ");
            String enteredUsername = sc.nextLine().trim();

            System.out.print("Enter password: ");
            String enteredPassword = sc.nextLine();

            if (username.equals(enteredUsername)
                    && password.equals(enteredPassword)) {
                System.out.println("Login successful!");
                return true;
            }

            System.out.println("Incorrect credentials. Attempts remaining: "
                    + (3 - attempt));
        }

        return false;
    }

    private static void createAccount(CustomerRepository repository) {
        System.out.println("\n--- Create Account ---");

        System.out.print("Enter account number: ");
        int acno = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter email: ");
        String email = sc.nextLine().trim();

        System.out.print("Enter account type (Savings/Current): ");
        String accountType = sc.nextLine().trim();

        if (acno <= 0 || name.isBlank() || email.isBlank()) {
            System.out.println("Please enter valid account details.");
            return;
        }

        if (!accountType.equalsIgnoreCase("Savings")
                && !accountType.equalsIgnoreCase("Current")) {
            System.out.println("Account type must be Savings or Current.");
            return;
        }

        Customer customer = new Customer();
        customer.setAcno(acno);
        customer.setCname(name);
        customer.setEmail(email);
        customer.setAccounttype(accountType);
        customer.setBal(BigDecimal.ZERO);

        repository.saveCustomer(customer);
        System.out.println("Account created successfully!");
    }

    private static void viewAccounts(CustomerRepository repository) {
        System.out.println("\n--- All Accounts ---");

        List<Customer> customers = repository.findAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Customer customer : customers) {
            printCustomer(customer);
        }
    }

    private static void searchAccount(CustomerRepository repository) {
        System.out.println("\n--- Search Account ---");
        System.out.print("Enter account number: ");

        int acno = Integer.parseInt(sc.nextLine().trim());
        Customer customer = repository.findCustomerByAccountNumber(acno);

        if (customer == null) {
            System.out.println("Account not found.");
        } else {
            printCustomer(customer);
        }
    }

    private static void deposit(CustomerRepository repository) {
        System.out.println("\n--- Deposit Money ---");

        System.out.print("Enter account number: ");
        int acno = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = new BigDecimal(sc.nextLine().trim());

        repository.depositMoney(acno, amount);
        System.out.println("Deposit successful!");
    }

    private static void withdraw(CustomerRepository repository) {
        System.out.println("\n--- Withdraw Money ---");

        System.out.print("Enter account number: ");
        int acno = Integer.parseInt(sc.nextLine().trim());

        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = new BigDecimal(sc.nextLine().trim());

        repository.withdrawMoney(acno, amount);
        System.out.println("Withdrawal successful!");
    }

    private static void viewTransactions(CustomerRepository repository) {
        System.out.println("\n--- Transaction History ---");

        List<Transaction> transactions = repository.findAllTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    private static void deleteAccount(CustomerRepository repository) {
        System.out.println("\n--- Delete Account ---");

        System.out.print("Enter account number: ");
        int acno = Integer.parseInt(sc.nextLine().trim());

        repository.deleteCustomer(acno);
        System.out.println("Account deleted successfully!");
    }

    private static void printCustomer(Customer customer) {
        System.out.println("--------------------------------");
        System.out.println("Account Number : " + customer.getAcno());
        System.out.println("Customer Name  : " + customer.getCname());
        System.out.println("Email          : " + customer.getEmail());
        System.out.println("Account Type   : " + customer.getAccounttype());
        System.out.println("Balance        : ₹" + customer.getBal());
        System.out.println("--------------------------------");
    }
}
