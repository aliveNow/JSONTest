package ru.skypathway.jsontest.data.dao;

/**
 * Created by samsmariya on 11.10.17.
 */

public class Comment extends BaseObject {
    String name;
    String email;
    String body;

    Comment() {}

    public Comment(int id, String name, String email, String body) {
        super(id);
        this.name = name;
        this.email = email;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }
}
