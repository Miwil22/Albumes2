package org.example.users.exceptions;

public abstract class UsersException extends RuntimeException {
    public UsersException(String message) {
        super(message);
    }
}
