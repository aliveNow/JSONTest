package ru.skypathway.jsontest.utils;

import ru.skypathway.jsontest.data.BaseObjectType;

/**
 * Created by samsmariya on 10.10.17.
 */

public class Constants {
    public static final String NAMESPACE_PREFIX = "ru.skypathway.jsontest";

    public static class Extras {

        private static String createExtra(String suffix){
            return Constants.NAMESPACE_PREFIX + ".extra." + suffix;
        }

        public static final String OBJECT_IDS = createExtra("object_ids");

    }

    public static class Loaders {
        public static final int POST = BaseObjectType.POSTS.ordinal();
        public static final int COMMENT = BaseObjectType.COMMENTS.ordinal();
        public static final int PHOTO = BaseObjectType.PHOTOS.ordinal();
        public static final int TODO = BaseObjectType.TODOS.ordinal();
        public static final int USERS = BaseObjectType.USERS.ordinal();
    }

}
