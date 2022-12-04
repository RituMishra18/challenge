package com.dws.challenge.exception;

public class CheckBalanceException extends APIException {

    public CheckBalanceException(String message, String errorCode) {
        super(message, errorCode);
    }

}
