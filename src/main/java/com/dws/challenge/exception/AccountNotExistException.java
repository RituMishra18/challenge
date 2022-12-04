package com.dws.challenge.exception;

import org.springframework.http.HttpStatus;

public class AccountNotExistException extends APIException {

    public AccountNotExistException(String message, String errorCode) {
        super(message, errorCode);
    }

    public AccountNotExistException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
}
