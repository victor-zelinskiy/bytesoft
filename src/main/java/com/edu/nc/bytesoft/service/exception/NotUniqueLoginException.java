package com.edu.nc.bytesoft.service.exception;


public class NotUniqueLoginException extends Exception {

    public NotUniqueLoginException() {
        super();
    }

    public NotUniqueLoginException(String message) {
        super(message);
    }

    public NotUniqueLoginException(Throwable cause) {
        super(cause);
    }

    public NotUniqueLoginException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
