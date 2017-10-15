package ru.skypathway.jsontest.data;

/**
 * Created by samsmariya on 15.10.17.
 */
public enum BaseObjectType {
    NONE("", 0, 0),
    POSTS("posts", 1, 100),
    COMMENTS("comments", 1, 500),
    USERS("users", 1, 10),
    PHOTOS("photos", 1, 5000),
    TODOS("todos", 1, 200);

    public final String value;
    public final int minId;
    public final int maxId;

    BaseObjectType(String value, int minId, int maxId) {
        this.value = value;
        this.minId = minId;
        this.maxId = maxId;
    }
}
