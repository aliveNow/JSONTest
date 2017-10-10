package ru.skypathway.jsontest.data;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.data.dao.JSONConverter;
import ru.skypathway.jsontest.utils.Constants;

/**
 * Created by samsmariya on 10.10.17.
 */

public class ArrayLoader<T extends BaseObject> extends BaseLoader<List<T>> {
    private static final String TAG = ArrayLoader.class.getSimpleName();

    public ArrayLoader(Context context,
                       Constants.CategoryEnum category,
                       int[] objectIds) {
        super(context, category, objectIds);
    }

    @Override
    protected List<T> convertToResult(@NonNull List<String> strings) throws JSONException {
        List<T> result = new ArrayList<>();
        for (String jsonString : strings) {
            JSONObject jsonBody = new JSONObject(jsonString);
            T baseObject = JSONConverter.getBaseObject(jsonBody, mCategory);
            if (baseObject != null) {
                result.add(baseObject);
            }
        }
        return result;
    }
}
