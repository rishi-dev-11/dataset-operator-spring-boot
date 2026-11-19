package com.dataset.operator.exception;

public class InvalidRecordPayloadException extends RuntimeException {

    public InvalidRecordPayloadException(String message) {
        super(message);
    }

    public InvalidRecordPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
