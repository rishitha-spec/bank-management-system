package com.rishi.bank_management_system;

public class InvalidAccount extends RuntimeException {

    public InvalidAccount(String message) {
        super(message);
    }
}
