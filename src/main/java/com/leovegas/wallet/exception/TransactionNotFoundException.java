package com.leovegas.wallet.exception;


public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
