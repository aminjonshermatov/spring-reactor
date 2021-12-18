package com.learnreactivespring.fluxandmonoplayground.exception;

public class CustomException extends Throwable {
    private final String message;

    @Override
    public String getMessage() {
        return this.message;
    }

    public CustomException(Throwable e) {
        this.message = e.getMessage();
    }
}
