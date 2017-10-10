package ru.skypathway.jsontest.data;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.data.dao.JSONConverter;
import ru.skypathway.jsontest.utils.Constants;

/**
 * Created by samsmariya on 10.10.17.
 */

public class ObjectLoader<D extends BaseObject> extends BaseLoader<D> {
    private static final String TAG = ObjectLoader.class.getSimpleName();

    public ObjectLoader(Context context,
                        Constants.CategoryEnum category,
                        int objectId) {
        super(context, category, objectId);
    }

    @Override
    protected D convertToResult(@NonNull List<String> strings) throws JSONException {
        String jsonString = strings.get(0);
        JSONObject jsonBody = new JSONObject(jsonString);
        D result = (D) JSONConverter.getPost(jsonBody);
        return result;
    }
}
