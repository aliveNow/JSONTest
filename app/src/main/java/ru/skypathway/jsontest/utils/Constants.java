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

    }

    public static class Loaders {
        public static final int POSTS = 1;
        public static final int USERS = 2;
    }

    public enum CategoryEnum {
        POSTS(Constants.POSTS),
        COMMENTS(Constants.COMMENTS),
        USERS(Constants.USERS),
        PHOTOS(Constants.PHOTOS),
        TODOS(Constants.TODOS);

        public final String value;

        CategoryEnum(String value) {
            this.value = value;
        }
    }

}
