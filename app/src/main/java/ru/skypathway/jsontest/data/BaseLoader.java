package ru.skypathway.jsontest.data;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.skypathway.jsontest.utils.Constants;

/**
 * Created by samsmariya on 10.10.17.
 */

public abstract class BaseLoader<D> extends AsyncTaskLoader<D> {
    private static final String TAG = BaseLoader.class.getSimpleName();

    protected Constants.CategoryEnum mCategory;
    protected int mObjectId;

    public BaseLoader(Context context,
                      Constants.CategoryEnum category,
                      int objectId) {
        super(context);
        mCategory = category;
        mObjectId = objectId;
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
    }

    public String getUrlString(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        // TODO: 10.10.17 java 7 и try с ресурсами
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            return sb.toString();
        } finally {
            connection.disconnect();
        }
    }
}
