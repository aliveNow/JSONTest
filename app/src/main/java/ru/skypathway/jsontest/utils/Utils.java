package ru.skypathway.jsontest.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by samsmariya on 11.10.17.
 */

public class Utils {

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
        return obj;
    }

    public static void hideSoftInputKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
    }

}
