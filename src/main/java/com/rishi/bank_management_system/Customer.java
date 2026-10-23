package com.rishi.bank_management_system;

import java.math.BigDecimal;

public class Customer {

    private int acno;
    private String cname;
    private String email;
    private String accounttype;
    private BigDecimal bal;

    public Customer() {
    }

    public int getAcno() {
        return acno;
    }

    public void setAcno(int acno) {
        this.acno = acno;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public BigDecimal getBal() {
        return bal;
    }

    public void setBal(BigDecimal bal) {
        this.bal = bal;
    }
}