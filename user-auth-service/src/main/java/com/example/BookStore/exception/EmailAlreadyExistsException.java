package com.example.BookStore.exception;

/**
 * Thrown when someone tries to register with an email that's already taken.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}