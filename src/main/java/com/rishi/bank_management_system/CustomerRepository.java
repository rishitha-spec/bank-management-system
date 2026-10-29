package com.rishi.bank_management_system;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CustomerRepository {

private final JdbcTemplate jdbcTemplate;

public CustomerRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
}

// Map database rows to Customer objects
private final RowMapper<Customer> customerRowMapper =
        new RowMapper<Customer>() {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum)
            throws SQLException {

        Customer customer = new Customer();

        customer.setAcno(rs.getInt("acno"));
        customer.setCname(rs.getString("cname"));
        customer.setEmail(rs.getString("email"));
        customer.setAccounttype(rs.getString("accounttype"));
        customer.setBal(rs.getBigDecimal("bal"));

        return customer;
    }
};

// Create a new customer account
public void saveCustomer(Customer customer) {

    String sql = """
            INSERT INTO customers
            (acno, cname, email, accounttype, bal)
            VALUES (?, ?, ?, ?, ?)
            """;

    jdbcTemplate.update(
            sql,
            customer.getAcno(),
            customer.getCname(),
            customer.getEmail(),
            customer.getAccounttype(),
            customer.getBal()
    );
}

// Display all customers
public List<Customer> findAllCustomers() {

    String sql = """
            SELECT acno, cname, email, accounttype, bal
            FROM customers
            ORDER BY acno
            """;

    return jdbcTemplate.query(sql, customerRowMapper);
}

// Search for a customer using account number
public Customer findCustomerByAccountNumber(int acno) {

    String sql = """
            SELECT acno, cname, email, accounttype, bal
            FROM customers
            WHERE acno = ?
            """;

    List<Customer> customers = jdbcTemplate.query(
            sql,
            customerRowMapper,
            acno
    );

    if (customers.isEmpty()) {
        return null;
    }

    return customers.get(0);
}

// Deposit money
@Transactional
public String depositMoney(int acno, BigDecimal amount) {

    if (acno <= 0) {
        throw new InvalidAccount(
                "Enter a valid account number."
        );
    }

    if (amount == null
            || amount.compareTo(BigDecimal.ZERO) <= 0) {

        throw new InvalidAmt(
                "Deposit amount must be greater than zero."
        );
    }

    BigDecimal maximumAmount = new BigDecimal("50000");

    if (amount.compareTo(maximumAmount) > 0) {
        throw new InvalidAmt(
                "Maximum deposit amount is ₹50,000."
        );
    }

    Customer customer = findCustomerByAccountNumber(acno);

    if (customer == null) {
        throw new InvalidAccount(
                "No account found with number " + acno + "."
        );
    }

    String updateSql = """
            UPDATE customers
            SET bal = bal + ?
            WHERE acno = ?
            """;

    jdbcTemplate.update(updateSql, amount, acno);

    String transactionSql = """
            INSERT INTO transactions
            (transaction_type, acno, amount)
            VALUES (?, ?, ?)
            """;

    jdbcTemplate.update(
            transactionSql,
            "DEPOSIT",
            acno,
            amount
    );

    return "SUCCESS";
}

// Withdraw money
@Transactional
public String withdrawMoney(int acno, BigDecimal amount) {

    BigDecimal minimumAmount = new BigDecimal("100");

    if (acno <= 0) {
        throw new InvalidAccount(
                "Enter a valid account number."
        );
    }

    if (amount == null
            || amount.compareTo(minimumAmount) < 0) {

        throw new InvalidAmt(
                "Withdrawal amount must be at least ₹100."
        );
    }

    String updateSql = """
            UPDATE customers
            SET bal = bal - ?
            WHERE acno = ?
              AND bal >= ?
            """;

    int rowsUpdated = jdbcTemplate.update(
            updateSql,
            amount,
            acno,
            amount
    );

    if (rowsUpdated == 0) {

        Customer customer =
                findCustomerByAccountNumber(acno);

        if (customer == null) {
            throw new InvalidAccount(
                    "No account found with number "
                            + acno + "."
            );
        }

        throw new InsufficientFunds(
                "Insufficient balance. "
                        + "Withdrawal was not completed."
        );
    }

    String transactionSql = """
            INSERT INTO transactions
            (transaction_type, acno, amount)
            VALUES (?, ?, ?)
            """;

    jdbcTemplate.update(
            transactionSql,
            "WITHDRAWAL",
            acno,
            amount
    );

    return "SUCCESS";
}

// Display transaction history
public List<Transaction> findAllTransactions() {

    String sql = """
            SELECT tid, transaction_type, acno,
                   amount, transaction_date
            FROM transactions
            ORDER BY transaction_date DESC
            """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> {

        Transaction transaction = new Transaction();

        transaction.setTid(rs.getInt("tid"));
        transaction.setTransactionType(
                rs.getString("transaction_type")
        );
        transaction.setAcno(rs.getInt("acno"));
        transaction.setAmount(rs.getBigDecimal("amount"));

        if (rs.getTimestamp("transaction_date") != null) {
            transaction.setTransactionDate(
                    rs.getTimestamp("transaction_date")
                            .toLocalDateTime()
            );
        }

        return transaction;
    });
}

// Delete a customer account
// Accounts with transaction history cannot be deleted.
@Transactional
public String deleteCustomer(int acno) {

    if (acno <= 0) {
        throw new InvalidAccount(
                "Enter a valid account number."
        );
    }

    Customer customer = findCustomerByAccountNumber(acno);

    if (customer == null) {
        return "ACCOUNT_NOT_FOUND";
    }

    String transactionCheckSql = """
            SELECT COUNT(*)
            FROM transactions
            WHERE acno = ?
            """;

    Integer transactionCount = jdbcTemplate.queryForObject(
            transactionCheckSql,
            Integer.class,
            acno
    );

    if (transactionCount != null && transactionCount > 0) {
        return "HAS_TRANSACTIONS";
    }

    String deleteSql = """
            DELETE FROM customers
            WHERE acno = ?
            """;

    int rowsDeleted = jdbcTemplate.update(deleteSql, acno);

    return rowsDeleted > 0
            ? "SUCCESS"
            : "ACCOUNT_NOT_FOUND";
}

}
