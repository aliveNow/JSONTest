package ru.skypathway.jsontest.data.dao;

/**
 * Created by samsmariya on 10.10.17.
 */

public class BaseObject {
    int id;

    BaseObject() {}

    BaseObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
