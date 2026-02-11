package org.example.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNameOrEmailExists extends UserException {
    public UserNameOrEmailExists(String message) {
        super(message);
    }
}