package ru.skypathway.jsontest.data.dao;

/**
 * Created by samsmariya on 11.10.17.
 */

public class Photo extends BaseObject {
    String title;
    String url;
    String thumbnailUrl;

    Photo() {}

    public Photo(int id, String title, String url, String thumbnailUrl) {
        super(id);
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
