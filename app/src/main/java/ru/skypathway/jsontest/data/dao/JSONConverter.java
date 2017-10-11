package ru.skypathway.jsontest.data.dao;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.skypathway.jsontest.utils.Constants;

/**
 * Created by samsmariya on 10.10.17.
 */

public class JSONConverter {
    public static final String TAG = JSONConverter.class.getSimpleName();

    public static<T extends BaseObject> T getBaseObject(JSONObject jsonObject, Constants.CategoryEnum category) {
        if (jsonObject == null) {
            return null;
        }
        switch (category) {
            case POSTS: return (T) getPost(jsonObject);
            case COMMENTS: return (T) getComment(jsonObject);
            case USERS: return (T) getUser(jsonObject);
            case PHOTOS:
                break;
            case TODOS:
                break;
        }
        return null;
    }

    public static<T extends BaseObject> List<T> getObjectsList(List<JSONObject> jsonObjects, Constants.CategoryEnum category) {
        List<T> listResult = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            T object = getBaseObject(jsonObject, category);
            if (object != null) {
                listResult.add(object);
            }
        }
        return listResult;
    }

    static Post getPost(@NonNull JSONObject jsonObject) {
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

    static Comment getComment(@NonNull JSONObject jsonObject) {
        try {
            Comment newObject = new Comment();
            newObject.id = jsonObject.getInt("id");
            newObject.name = jsonObject.getString("name");
            newObject.email = jsonObject.getString("email");
            newObject.body = jsonObject.getString("body");
            return newObject;
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return null;
    }

    static User getUser(@NonNull JSONObject jsonObject) {
        try {
            User newObject = new User();
            newObject.id = jsonObject.getInt("id");
            newObject.name = jsonObject.getString("name");
            newObject.username = jsonObject.getString("username");
            return newObject;
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return null;
    }
    
}
