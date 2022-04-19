package com.example.demo.exceptions;

public class UserExistsException extends RuntimeException{
    public UserExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
