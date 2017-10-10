package ru.skypathway.jsontest.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    public D loadInBackground() {
        try {
            String url = Uri.parse("https://jsonplaceholder.typicode.com/")
                    .buildUpon()
                    .appendPath(mCategory.value)
                    .appendPath(Integer.toString(mObjectId))
                    .build().toString();
            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            D result = (D) JSONConverter.getPost(jsonBody);
            return result;
        } catch (IOException ioe) {
            Log.e(TAG, "Failed ", ioe);
        }catch (JSONException je){
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return null;
    }
}
