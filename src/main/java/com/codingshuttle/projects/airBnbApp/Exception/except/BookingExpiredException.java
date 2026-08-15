package com.codingshuttle.projects.airBnbApp.Exception.except;

public class BookingExpiredException extends RuntimeException {
    public BookingExpiredException(String message) {
        super(message);
    }
}
