package com.example.geektrust.exception;

public class InvalidCommandException extends Exception {
    
    public InvalidCommandException(String message) {
        super(message);
    }
    
    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}