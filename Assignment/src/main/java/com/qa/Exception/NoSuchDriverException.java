package com.qa.Exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoSuchDriverException extends Exception {
    public NoSuchDriverException(String exception) {
        super(exception);
    }

    public NoSuchDriverException() {
        super();
    }

    public NoSuchDriverException(String exception, Throwable throwable) {
        super(exception, throwable);
    }
}
