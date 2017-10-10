package ru.skypathway.jsontest.data.dao;

/**
 * Created by samsmariya on 11.10.17.
 */

public class User extends BaseObject {
    String name;
    String username;

    User() {}

    public User(int id, String name, String username) {
        super(id);
        this.name = name;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}
