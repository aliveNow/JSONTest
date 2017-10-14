package ru.skypathway.jsontest.utils;

import ru.skypathway.jsontest.data.ExceptionWrapper;

/**
 * Created by samsmariya on 13.10.17.
 */

public interface CanHandleExceptionWrapper {
    boolean showError(ExceptionWrapper exception);
    void hideError(ExceptionWrapper exception);
    void hideAllErrors();
}
