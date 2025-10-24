package com.quiz.accesscontrol.exception;

public class EmailDeliveryFailedException extends RuntimeException {
    public EmailDeliveryFailedException(String message) {
        super(message);
    }
}
