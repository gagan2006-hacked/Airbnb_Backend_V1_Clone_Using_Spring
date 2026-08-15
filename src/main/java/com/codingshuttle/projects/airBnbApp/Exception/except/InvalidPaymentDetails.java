package com.codingshuttle.projects.airBnbApp.Exception.except;

public class InvalidPaymentDetails extends RuntimeException {
    public InvalidPaymentDetails(String message) {
        super(message);
    }
}
