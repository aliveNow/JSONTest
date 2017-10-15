package ru.skypathway.jsontest.data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.data.dao.JSONConverter;
import ru.skypathway.jsontest.utils.Utils;

/**
 * Created by samsmariya on 10.10.17.
 */

public class BaseObjectLoader<D extends BaseObject> extends BaseLoader<D> {
    private static final String TAG = BaseObjectLoader.class.getSimpleName();
    public static final String BASE_URL_STRING = "https://jsonplaceholder.typicode.com/";

    protected BaseObjectType mType;

    public BaseObjectLoader(@NonNull Context context,
                            @NonNull BaseObjectType type,
                            @NonNull int[] objectIds) {
        super(context, objectIds);
        Utils.requireNonNull(type, TAG + ": Type can't be null");
        mType = type;
    }

    public BaseObjectLoader(@NonNull Context context,
                            @NonNull BaseObjectType type,
                            int objectId) {
        this(context, type, new int[]{objectId});
    }

    @Override
    protected @NonNull D convertToResult(int objectId, @NonNull String jsonString) throws JSONException {
        JSONObject jsonBody = new JSONObject(jsonString);
        D result = JSONConverter.getBaseObject(jsonBody, mType);
        return result;
    }

    @Override
    protected @NonNull Uri getLoadingUri(int objectId) {
        return Uri.parse(BASE_URL_STRING)
                .buildUpon()
                .appendPath(mType.value)
                .appendPath(Integer.toString(objectId))
                .build();
    }
}
