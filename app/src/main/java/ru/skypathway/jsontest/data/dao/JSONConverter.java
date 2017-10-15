package ru.skypathway.jsontest.data.dao;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.skypathway.jsontest.data.BaseObjectType;

/**
 * Created by samsmariya on 10.10.17.
 */

public class JSONConverter {

    public static<T extends BaseObject> T getBaseObject(JSONObject jsonObject,
                                                        BaseObjectType type) throws JSONException{
        if (jsonObject == null) {
            return null;
        }
        switch (type) {
            case POSTS: return (T) getPost(jsonObject);
            case COMMENTS: return (T) getComment(jsonObject);
            case USERS: return (T) getUser(jsonObject);
            case PHOTOS: return (T) getPhoto(jsonObject);
            case TODOS: return (T) getToDo(jsonObject);
        }
        return null;
    }

    public static<T extends BaseObject> List<T> getObjectsList(List<JSONObject> jsonObjects,
                                                               BaseObjectType type) throws JSONException {
        List<T> listResult = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            T object = getBaseObject(jsonObject, type);
            if (object != null) {
                listResult.add(object);
            }
        }
        return listResult;
    }

    static Post getPost(@NonNull JSONObject jsonObject) throws JSONException {
        Post newObject = new Post();
        newObject.id = jsonObject.getInt("id");
        newObject.title = jsonObject.getString("title");
        newObject.body = jsonObject.getString("body");
        return newObject;
    }

    static Comment getComment(@NonNull JSONObject jsonObject) throws JSONException {
        Comment newObject = new Comment();
        newObject.id = jsonObject.getInt("id");
        newObject.name = jsonObject.getString("name");
        newObject.email = jsonObject.getString("email");
        newObject.body = jsonObject.getString("body");
        return newObject;
    }

    static Photo getPhoto(@NonNull JSONObject jsonObject) throws JSONException {
        Photo newObject = new Photo();
        newObject.id = jsonObject.getInt("id");
        newObject.title = jsonObject.getString("title");
        newObject.url = jsonObject.getString("url");
        newObject.thumbnailUrl = jsonObject.getString("thumbnailUrl");
        return newObject;
    }

    static User getUser(@NonNull JSONObject jsonObject) throws JSONException {
        User newObject = new User();
        newObject.id = jsonObject.getInt("id");
        newObject.name = jsonObject.getString("name");
        newObject.username = jsonObject.getString("username");
        return newObject;
    }

    static ToDo getToDo(@NonNull JSONObject jsonObject) throws JSONException {
        ToDo newObject = new ToDo();
        newObject.id = jsonObject.getInt("id");
        newObject.title = jsonObject.getString("title");
        newObject.completed = jsonObject.getBoolean("completed");
        return newObject;
    }
    
}
