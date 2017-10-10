package ru.skypathway.jsontest.data.dao;

/**
 * Created by samsmariya on 10.10.17.
 */

public class Post extends BaseObject {
    String title;
    String body;

    Post() {}

    public Post(int id, String title, String body) {
        super(id);
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
