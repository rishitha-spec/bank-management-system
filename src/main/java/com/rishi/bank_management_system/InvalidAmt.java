package com.rishi.bank_management_system;

public class InvalidAmt extends RuntimeException {

    public InvalidAmt(String message) {
        super(message);
    }
}
