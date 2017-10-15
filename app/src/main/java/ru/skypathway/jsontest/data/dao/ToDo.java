package ru.skypathway.jsontest.data.dao;

/**
 * Created by samsmariya on 14.10.17.
 */

public class ToDo extends BaseObject {
    String title;
    boolean completed;

    ToDo() {}

    public ToDo(int id, String title, boolean completed) {
        super(id);
        this.title = title;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }
}
