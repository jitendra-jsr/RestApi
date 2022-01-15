package com.qa.Exception;

public class NoSuchCityException extends Exception {
    public NoSuchCityException() {
        super();
    }

    public NoSuchCityException(String message) {
        super(message);
    }

    public NoSuchCityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchCityException(Throwable cause) {
        super(cause);
    }

    protected NoSuchCityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

}
