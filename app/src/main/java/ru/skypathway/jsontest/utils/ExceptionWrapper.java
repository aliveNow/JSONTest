package ru.skypathway.jsontest.utils;

/**
 * Created by samsmariya on 13.10.17.
 */

public class ExceptionWrapper extends Exception {

    public ExceptionWrapper(Exception exception, String message) {
        super(message, exception);
    }

    public String getCauseDescription() {
        return getCause().getLocalizedMessage();
    }

    @Override
    public Exception getCause() {
        return (Exception) super.getCause();
    }

}
