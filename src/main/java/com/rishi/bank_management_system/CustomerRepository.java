package com.rishi.bank_management_system;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Create a new customer account
    public int saveCustomer(Customer customer) {

        String sql = """
                INSERT INTO customers
                (acno, cname, email, accounttype, bal)
                VALUES (?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                customer.getAcno(),
                customer.getCname(),
                customer.getEmail(),
                customer.getAccounttype(),
                customer.getBal() == null
                        ? BigDecimal.ZERO
                        : customer.getBal()
        );
    }

    // Retrieve all customers
    public List<Customer> findAllCustomers() {

        String sql = "SELECT * FROM customers ORDER BY acno";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Customer customer = new Customer();

            customer.setAcno(rs.getInt("acno"));
            customer.setCname(rs.getString("cname"));
            customer.setEmail(rs.getString("email"));
            customer.setAccounttype(rs.getString("accounttype"));
            customer.setBal(rs.getBigDecimal("bal"));

            return customer;
        });
    }

    // Find a customer by account number
    public Customer findCustomerByAccountNumber(int acno) {

        String sql = "SELECT * FROM customers WHERE acno = ?";

        List<Customer> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {
                    Customer customer = new Customer();

                    customer.setAcno(rs.getInt("acno"));
                    customer.setCname(rs.getString("cname"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAccounttype(rs.getString("accounttype"));
                    customer.setBal(rs.getBigDecimal("bal"));

                    return customer;
                },
                acno
        );

        return results.isEmpty() ? null : results.get(0);
    }

    // Deposit money and record the transaction
    @Transactional
    public int depositMoney(int acno, BigDecimal amount) {

        if (acno <= 0 || amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        String updateSql =
                "UPDATE customers SET bal = bal + ? WHERE acno = ?";

        int rowsUpdated = jdbcTemplate.update(
                updateSql,
                amount,
                acno
        );

        if (rowsUpdated == 1) {

            String insertSql = """
                    INSERT INTO transactions
                    (transaction_type, acno, amount)
                    VALUES (?, ?, ?)
                    """;

            jdbcTemplate.update(
                    insertSql,
                    "DEPOSIT",
                    acno,
                    amount
            );
        }

        return rowsUpdated;
    }

    // Withdraw money only when sufficient balance is available
    @Transactional
    public String withdrawMoney(int acno, BigDecimal amount) {

        if (acno <= 0 || amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "INVALID_AMOUNT";
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

            Customer customer = findCustomerByAccountNumber(acno);

            if (customer == null) {
                return "ACCOUNT_NOT_FOUND";
            }

            return "INSUFFICIENT_BALANCE";
        }

        String insertSql = """
                INSERT INTO transactions
                (transaction_type, acno, amount)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(
                insertSql,
                "WITHDRAWAL",
                acno,
                amount
        );

        return "SUCCESS";
    }

    // Retrieve transaction history for all accounts
    public List<Transaction> findAllTransactions() {

        String sql = """
                SELECT tid, transaction_type, acno, amount, transaction_date
                FROM transactions
                ORDER BY tid DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Transaction transaction = new Transaction();

            transaction.setTid(rs.getInt("tid"));
            transaction.setTransactionType(
                    rs.getString("transaction_type"));
            transaction.setAcno(rs.getInt("acno"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setTransactionDate(
                    rs.getTimestamp("transaction_date").toLocalDateTime());

            return transaction;
        });
    }
    public String deleteCustomer(int acno) {

    String checkAccountSql =
            "SELECT COUNT(*) FROM customers WHERE acno = ?";

    Integer accountCount = jdbcTemplate.queryForObject(
            checkAccountSql, Integer.class, acno);

    if (accountCount == null || accountCount == 0) {
        return "ACCOUNT_NOT_FOUND";
    }

    String checkTransactionsSql =
            "SELECT COUNT(*) FROM transactions WHERE acno = ?";

    Integer transactionCount = jdbcTemplate.queryForObject(
            checkTransactionsSql, Integer.class, acno);

    if (transactionCount != null && transactionCount > 0) {
        return "HAS_TRANSACTIONS";
    }

    String deleteSql = "DELETE FROM customers WHERE acno = ?";

    int deletedRows = jdbcTemplate.update(deleteSql, acno);

    return deletedRows == 1 ? "SUCCESS" : "FAILED";
}
    
}

