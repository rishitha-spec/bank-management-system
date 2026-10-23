package com.rishi.bank_management_system;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CustomerController {

private final CustomerRepository customerRepository;

public CustomerController(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
}

@GetMapping("/create-account")
public String showCreateAccountForm() {
    return "create-account";
}

@PostMapping("/create-account")
public String createAccount(
        @RequestParam int acno,
        @RequestParam String cname,
        @RequestParam(required = false) String email,
        @RequestParam String accounttype,
        Model model,
        RedirectAttributes redirectAttributes) {

    if (acno <= 0 || cname.isBlank()) {
        model.addAttribute("error",
                "Enter a valid account number and customer name.");
        return "create-account";
    }

    if (!accounttype.equals("Savings")
            && !accounttype.equals("Current")) {
        model.addAttribute("error", "Select a valid account type.");
        return "create-account";
    }

    Customer customer = new Customer();
    customer.setAcno(acno);
    customer.setCname(cname.trim());
    customer.setEmail(
            email == null || email.isBlank() ? null : email.trim());
    customer.setAccounttype(accounttype);
    customer.setBal(BigDecimal.ZERO);

    try {
        customerRepository.saveCustomer(customer);

        redirectAttributes.addFlashAttribute(
                "success", "Account created successfully!");

        return "redirect:/create-account";

    } catch (org.springframework.dao.DuplicateKeyException ex) {
        model.addAttribute("error",
                "That account number already exists.");
        return "create-account";
    }
}
@GetMapping("/customers")
public String viewCustomers(Model model) {
    model.addAttribute(
        "customers",
        customerRepository.findAllCustomers()
    );

    return "customers";
}
@GetMapping("/search-customer")
public String showSearchPage() {
    return "search-customer";
}

@PostMapping("/search-customer")
public String searchCustomer(
        @RequestParam int acno,
        Model model) {

    Customer customer =
            customerRepository.findCustomerByAccountNumber(acno);

    if (customer != null) {
        model.addAttribute("customer", customer);
    } else {
        model.addAttribute(
                "error",
                "No customer found with account number " + acno
        );
    }

    return "search-customer";
}
@GetMapping("/deposit")
public String showDepositPage() {
    return "deposit";
}

@PostMapping("/deposit")
public String depositMoney(
        @RequestParam int acno,
        @RequestParam BigDecimal amount,
        Model model) {

    if (acno <= 0
        || amount == null
        || amount.compareTo(BigDecimal.ZERO) <= 0
        || amount.compareTo(new BigDecimal("50000")) > 0) {

    model.addAttribute(
            "error",
            "Enter a valid account number and a deposit amount between ₹0.01 and ₹50,000."
    );

    return "deposit";
}

    try {
        int rowsUpdated = customerRepository.depositMoney(acno, amount);

        if (rowsUpdated == 1) {
            model.addAttribute(
                    "success",
                    "Deposit successful! Amount deposited: " + amount
            );
        } else {
            model.addAttribute(
                    "error",
                    "No customer found with account number " + acno
            );
        }
    } catch (Exception ex) {
        model.addAttribute(
                "error",
                "The deposit could not be completed. Please try again."
        );
    }

    return "deposit";
}
@GetMapping("/withdraw")
public String showWithdrawPage() {
    return "withdraw";
}

// Process a withdrawal
@PostMapping("/withdraw")
public String withdrawMoney(
        @RequestParam int acno,
        @RequestParam BigDecimal amount,
        Model model) {

    if (acno <= 0 || amount == null
            || amount.compareTo(BigDecimal.ZERO) <= 0) {
        model.addAttribute(
                "error",
                "Enter a valid account number and an amount greater than zero."
        );
        return "withdraw";
    }

    try {
        String result = customerRepository.withdrawMoney(acno, amount);

        switch (result) {
            case "SUCCESS" ->
                    model.addAttribute(
                            "success",
                            "Withdrawal successful! Amount withdrawn: ₹" + amount
                    );

            case "ACCOUNT_NOT_FOUND" ->
                    model.addAttribute(
                            "error",
                            "No account found with number " + acno
                    );

            case "INSUFFICIENT_BALANCE" ->
                    model.addAttribute(
                            "error",
                            "Insufficient balance. Withdrawal was not completed."
                    );

            default ->
                    model.addAttribute(
                            "error",
                            "Invalid withdrawal details."
                    );
        }

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
public String showTransactions(Model model) {
    model.addAttribute(
            "transactions",
            customerRepository.findAllTransactions()
    );

    return "transactions";
}
// Display the delete-account page
@GetMapping("/delete-account")
public String showDeleteAccountPage() {
    return "delete-account";
}

// Process account deletion
@PostMapping("/delete-account")
public String deleteAccount(
        @RequestParam int acno,
        Model model) {

    if (acno <= 0) {
        model.addAttribute(
                "error",
                "Enter a valid account number."
        );
        return "delete-account";
    }

    try {
        String result = customerRepository.deleteCustomer(acno);

        switch (result) {
            case "SUCCESS" ->
                    model.addAttribute(
                            "success",
                            "Account " + acno + " deleted successfully."
                    );

            case "ACCOUNT_NOT_FOUND" ->
                    model.addAttribute(
                            "error",
                            "No account found with number " + acno
                    );

            case "HAS_TRANSACTIONS" ->
                    model.addAttribute(
                            "error",
                            "This account has transaction history and cannot be deleted."
                    );

            default ->
                    model.addAttribute(
                            "error",
                            "The account could not be deleted."
                    );
        }

    } catch (Exception ex) {
        model.addAttribute(
                "error",
                "The account could not be deleted. Please try again."
        );
    }

    return "delete-account";
}
}