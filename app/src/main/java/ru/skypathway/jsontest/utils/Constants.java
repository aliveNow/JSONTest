package ru.skypathway.jsontest.utils;

/**
 * Created by samsmariya on 10.10.17.
 */

public class Constants {
    public static final String NAMESPACE_PREFIX = "ru.skypathway.jsontest";

    public static final String POSTS = "posts";
    public static final String COMMENTS = "comments";
    public static final String USERS = "users";
    public static final String PHOTOS = "photos";
    public static final String TODOS = "todos";

    public static class Extras {

        private static String createExtra(String suffix){
            return Constants.NAMESPACE_PREFIX + ".extra." + suffix;
        }

        public static final String OBJECT_ID = createExtra("object_id");

    }

    public static class Loaders {
        public static final int POST = CategoryEnum.POSTS.ordinal();
        public static final int COMMENT = CategoryEnum.COMMENTS.ordinal();
        public static final int PHOTO = CategoryEnum.PHOTOS.ordinal();
        public static final int TODO = CategoryEnum.TODOS.ordinal();
        public static final int USERS = CategoryEnum.USERS.ordinal();
    }

    public enum CategoryEnum {
        NONE("", 0, 0),
        POSTS(Constants.POSTS, 1, 100),
        COMMENTS(Constants.COMMENTS, 1, 500),
        USERS(Constants.USERS, 1, 10),
        PHOTOS(Constants.PHOTOS, 1, 5000),
        TODOS(Constants.TODOS, 1, 200);

        public final String value;
        public final int minId;
        public final int maxId;

        CategoryEnum(String value, int minId, int maxId) {
            this.value = value;
            this.minId = minId;
            this.maxId = maxId;
        }
    }

}
