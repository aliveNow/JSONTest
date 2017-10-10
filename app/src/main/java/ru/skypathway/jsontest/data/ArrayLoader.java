package ru.skypathway.jsontest.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
                       int objectId) {
        super(context, category, objectId);
    }

    @Override
    public List<T> loadInBackground() {
        try {
            String url = Uri.parse("https://jsonplaceholder.typicode.com/")
                    .buildUpon()
                    .appendPath(mCategory.value)
                    .appendPath(Integer.toString(mObjectId))
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            List<T> result = new ArrayList<>();
            BaseObject baseObject = JSONConverter.getPost(jsonBody);
            result.add((T)baseObject);
            return result;
        } catch (IOException ioe) {
            Log.e(TAG, "Failed ", ioe);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return null;
    }
}
