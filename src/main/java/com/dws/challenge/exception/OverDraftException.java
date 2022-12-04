package com.dws.challenge.exception;

public class OverDraftException extends APIException {

    public OverDraftException(String message, String errorCode) {
        super(message, errorCode);
    }
}
