package com.rishi.bank_management_system;

import java.math.BigDecimal;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {

private final CustomerRepository customerRepository;

public CustomerController(
        CustomerRepository customerRepository) {

    this.customerRepository = customerRepository;
}

// Open account creation page
@GetMapping("/create-account")
public String showCreateAccountPage() {
    return "create-account";
}

// Create a new customer account
@PostMapping("/create-account")
public String createAccount(
        @RequestParam int acno,
        @RequestParam String cname,
        @RequestParam String email,
        @RequestParam String accounttype,
        Model model) {

    if (acno <= 0) {
        model.addAttribute(
                "error",
                "Enter a valid account number."
        );

        return "create-account";
    }

    if (cname == null || cname.trim().isEmpty()) {
        model.addAttribute(
                "error",
                "Customer name cannot be empty."
        );

        return "create-account";
    }

    if (!"Savings".equalsIgnoreCase(accounttype)
            && !"Current".equalsIgnoreCase(accounttype)) {

        model.addAttribute(
                "error",
                "Select a valid account type."
        );

        return "create-account";
    }

    Customer customer = new Customer();

    customer.setAcno(acno);
    customer.setCname(cname.trim());
    customer.setEmail(email);
    customer.setAccounttype(accounttype);
    customer.setBal(BigDecimal.ZERO);

    try {

        customerRepository.saveCustomer(customer);

        model.addAttribute(
                "success",
                "Account created successfully!"
        );

    } catch (DuplicateKeyException ex) {

        model.addAttribute(
                "error",
                "An account with this number already exists."
        );

    } catch (Exception ex) {
         ex.printStackTrace();
         model.addAttribute("error", "Unable to create account. Please try again.");
    }

    return "create-account";
}

// Display all customers
@GetMapping("/customers")
public String viewCustomers(Model model) {

    model.addAttribute(
            "customers",
            customerRepository.findAllCustomers()
    );

    return "customers";
}

// Search for a customer
@GetMapping("/search-customer")
public String searchCustomer(
        @RequestParam(required = false) Integer acno,
        Model model) {

    if (acno == null || acno <= 0) {
        model.addAttribute(
                "error",
                "Enter a valid account number."
        );

        return "search-customer";
    }

    Customer customer =
            customerRepository.findCustomerByAccountNumber(acno);

    if (customer == null) {

        model.addAttribute(
                "error",
                "No customer found with account number " + acno + "."
        );

    } else {

        model.addAttribute("customer", customer);
    }

    return "search-customer";
}

// Open deposit page
@GetMapping("/deposit")
public String showDepositPage() {
    return "deposit";
}

// Deposit money
@PostMapping("/deposit")
public String depositMoney(
        @RequestParam int acno,
        @RequestParam BigDecimal amount,
        Model model) {

    try {

        customerRepository.depositMoney(acno, amount);

        model.addAttribute(
                "success",
                "Deposit successful! Amount deposited: ₹" + amount
        );

    } catch (InvalidAccount | InvalidAmt ex) {

        model.addAttribute("error", ex.getMessage());

    } catch (Exception ex) {

        model.addAttribute(
                "error",
                "The deposit could not be completed. Please try again."
        );
    }

    return "deposit";
}

// Open withdrawal page
@GetMapping("/withdraw")
public String showWithdrawPage() {
    return "withdraw";
}

// Withdraw money
@PostMapping("/withdraw")
public String withdrawMoney(
        @RequestParam int acno,
        @RequestParam BigDecimal amount,
        Model model) {

    try {

        customerRepository.withdrawMoney(acno, amount);

        model.addAttribute(
                "success",
                "Withdrawal successful! Amount withdrawn: ₹" + amount
        );

    } catch (InvalidAccount
            | InvalidAmt
            | InsufficientFunds ex) {

        model.addAttribute("error", ex.getMessage());

    } catch (Exception ex) {

        model.addAttribute(
                "error",
                "The withdrawal could not be completed. Please try again."
        );
    }

    return "withdraw";
}

// Display transaction history
@GetMapping("/transactions")
public String viewTransactions(Model model) {

    model.addAttribute(
            "transactions",
            customerRepository.findAllTransactions()
    );

    return "transactions";
}

// Delete an account
@PostMapping("/delete-account")
public String deleteAccount(
        @RequestParam int acno,
        Model model) {

    try {

        String result = customerRepository.deleteCustomer(acno);

        if ("SUCCESS".equals(result)) {

            model.addAttribute(
                    "success",
                    "Account deleted successfully."
            );

        } else if ("ACCOUNT_NOT_FOUND".equals(result)) {

            model.addAttribute(
                    "error",
                    "Account not found."
            );

        } else if ("HAS_TRANSACTIONS".equals(result)) {

            model.addAttribute(
                    "error",
                    "This account has transaction history "
                            + "and cannot be deleted."
            );
        }

    } catch (InvalidAccount ex) {

        model.addAttribute("error", ex.getMessage());

    } catch (Exception ex) {

        model.addAttribute(
                "error",
                "Unable to delete account. Please try again."
        );
    }

    return "customers";
}

}
