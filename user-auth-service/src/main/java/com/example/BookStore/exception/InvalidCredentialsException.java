package com.example.BookStore.exception;

/**
 * Thrown when login email doesn't exist OR password doesn't match.
 * We use ONE generic exception/message for both cases on purpose -
 * telling the client specifically "email not found" vs "wrong password"
 * would let attackers figure out which emails are registered (security leak).
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}