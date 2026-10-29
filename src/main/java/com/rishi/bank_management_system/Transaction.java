
package com.rishi.bank_management_system;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int tid;
    private String transactionType;
    private int acno;
    private BigDecimal amount;
    private LocalDateTime transactionDate;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getAcno() {
        return acno;
    }

    public void setAcno(int acno) {
        this.acno = acno;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + tid
                + " | Type: " + transactionType
                + " | Account Number: " + acno
                + " | Amount: Rs. " + amount
                + " | Date: " + transactionDate;
    }
}
