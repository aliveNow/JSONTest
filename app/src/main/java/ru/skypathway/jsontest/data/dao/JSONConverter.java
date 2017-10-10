package ru.skypathway.jsontest.data.dao;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by samsmariya on 10.10.17.
 */

public class JSONConverter {
    public static final String TAG = JSONConverter.class.getSimpleName();
    
    public static Post getPost(JSONObject jsonObject) {
        try {
            Post newObject = new Post();
            newObject.id = jsonObject.getInt("id");
            newObject.title = jsonObject.getString("title");
            newObject.body = jsonObject.getString("body");
            return newObject;
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return null;
    }
    
}
