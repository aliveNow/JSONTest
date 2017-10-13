package ru.skypathway.jsontest.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
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

    public static boolean isEmptyArray(int[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        return false;
    }

    public static @NonNull String arrayToString(String delimiter, int[] array) {
        // увы TextUtils.join(",", array) на int[] не работает(
        if (isEmptyArray(array)) {
            return "";
        }
        if (array.length == 1) {
            return Integer.toString(array[0]);
        }
        StringBuilder builder = new StringBuilder();
        for (int i : array) {
            builder.append(i).append(delimiter+" ");
        }
        builder.delete(builder.length()-(delimiter.length()+1), builder.length());
        return builder.toString();
    }

    public static String getCategoryNameGenitive(Context context, Constants.CategoryEnum category) {
        return getCategoryNameGenitive(context, category, 1);
    }

    public static String getCategoryNameGenitive(Context context, Constants.CategoryEnum category, int size) {
        int strId = 0;
        switch (category) {
            case NONE: break;
            case POSTS: strId = R.plurals.plurals_post; break;
            case COMMENTS: strId = R.plurals.plurals_comment;break;
            case USERS: strId = R.plurals.plurals_user; break;
            case PHOTOS: strId = R.plurals.plurals_photos; break;
            case TODOS: strId = R.plurals.plurals_todos; break;
        }
        return strId > 0 ? context.getResources().getQuantityString(strId, size) : null;
    }

}
