package ru.skypathway.jsontest.data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.data.dao.JSONConverter;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.Utils;

/**
 * Created by samsmariya on 10.10.17.
 */

public class BaseObjectLoader<D extends BaseObject> extends BaseLoader<D> {
    private static final String TAG = BaseObjectLoader.class.getSimpleName();
    public static final String BASE_URL_STRING = "https://jsonplaceholder.typicode.com/";

    protected Constants.CategoryEnum mCategory; // FIXME: 10.10.17 заменить на строку или вытащить в пакет data?

    public BaseObjectLoader(Context context,
                            Constants.CategoryEnum category,
                            int[] objectIds) {
        super(context, objectIds);
        Utils.requireNonNull(category, TAG + ": Category can't be null");
        mCategory = category;
    }

    public BaseObjectLoader(Context context,
                            Constants.CategoryEnum category,
                            int objectId) {
        this(context, category, new int[]{objectId});
    }

    @Override
    protected @NonNull D convertToResult(@NonNull String jsonString) throws JSONException {
        JSONObject jsonBody = new JSONObject(jsonString);
        D result = JSONConverter.getBaseObject(jsonBody, mCategory);
        return result;
    }

    @Override
    protected @NonNull Uri getLoadingUri(int objectId) {
        return Uri.parse(BASE_URL_STRING)
                .buildUpon()
                .appendPath(mCategory.value)
                .appendPath(Integer.toString(objectId))
                .build();
    }
}
