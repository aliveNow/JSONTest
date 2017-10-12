package ru.skypathway.jsontest.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import ru.skypathway.jsontest.R;

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

    // FIXME: 12.10.17 rename and add all strings
    public static String getCategoryNameGenitive(Context context, Constants.CategoryEnum category) {
        int strId = 0;
        switch (category) {
            case NONE:
                break;
            case POSTS:
                strId = R.string.arg_post_genitive;
                break;
            case COMMENTS:
                strId = R.string.arg_comment_genitive;
                break;
            case USERS:
                strId = R.string.arg_user_genitive;
                break;
            case PHOTOS:
                strId = R.string.arg_photo_genitive;
                break;
            case TODOS:
                strId = R.string.arg_todo_genitive;
                break;
        }
        return strId > 0 ? context.getResources().getString(strId) : null;
    }

}
