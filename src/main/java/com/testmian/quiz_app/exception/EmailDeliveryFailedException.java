package com.testmian.quiz_app.exception;

public class EmailDeliveryFailedException extends RuntimeException {
    public EmailDeliveryFailedException(String message) {
        super(message);
    }
}
