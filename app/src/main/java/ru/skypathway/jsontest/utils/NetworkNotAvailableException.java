package ru.skypathway.jsontest.utils;

import java.io.IOException;

/**
 * Created by samsmariya on 13.10.17.
 */

public class NetworkNotAvailableException extends IOException {
    public NetworkNotAvailableException(String message) {
        super(message);
    }
}
