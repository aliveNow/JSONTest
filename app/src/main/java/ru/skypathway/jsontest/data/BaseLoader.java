package ru.skypathway.jsontest.data;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ru.skypathway.jsontest.R;
import ru.skypathway.jsontest.utils.Constants;

/**
 * Created by samsmariya on 10.10.17.
 *
 * Пример реализации частично подсмотрен отсюда:
 * https://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */

public abstract class BaseLoader<D> extends AsyncTaskLoader<BaseLoader.LoaderResult<D>> {
    private static final String TAG = BaseLoader.class.getSimpleName();
    public static final String BASE_URL_STRING = "https://jsonplaceholder.typicode.com/";

    protected Constants.CategoryEnum mCategory; // FIXME: 10.10.17 заменить на строку или вытащить в пакет data?
    protected int[] mObjectIds;
    protected D mCache;

    public BaseLoader(Context context,
                      Constants.CategoryEnum category,
                      int objectId) {
        super(context);
        mCategory = category;
        mObjectIds = new int[]{objectId};
    }

    public BaseLoader(Context context,
                      Constants.CategoryEnum category,
                      int[] objectIds) {
        super(context);
        mCategory = category;
        mObjectIds = objectIds;
    }

    @Override
    public LoaderResult<D> loadInBackground() {
        LoaderResult loaderResult;
        String url = null;
        try {
            if (mObjectIds == null) {
                return null;
            }
            // FIXME: 14.10.17 sleep поставлен, чтобы отследить работу progressbar
            SystemClock.sleep(1000);
            List<String> resultStrings = new ArrayList<>();
            for (int objectId : mObjectIds) {
                url = Uri.parse(BASE_URL_STRING)
                        .buildUpon()
                        .appendPath(mCategory.value)
                        .appendPath(Integer.toString(objectId))
                        .build().toString();
                String string = getUrlString(url);
                if (string != null) {
                    resultStrings.add(string);
                }
            }
            D resultObject = convertToResult(resultStrings);
            setCache(resultObject);
            loaderResult = new LoaderResult<>(resultObject, null);
        }catch (Exception exception) {
            Log.e(TAG, "Failed to load: " + url, exception);
            loaderResult = new LoaderResult<>(null, getDetailedException(exception, url));
        }
        return loaderResult;
    }

    protected abstract D convertToResult(@NonNull List<String> strings) throws JSONException;

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mCache != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(new LoaderResult<>(mCache, null));
        }

        if (takeContentChanged() || mCache == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(LoaderResult<D> data) {
        super.onCanceled(data);
        releaseCache();
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        releaseCache();
    }

    protected void setCache(D mCache) {
        this.mCache = mCache;
    }

    protected void releaseCache(){
        mCache = null;
    }

    public String getUrlString(String urlSpec) throws IOException {
        if (!isNetworkAvailable()) {
            String msg = getContext().getResources().getString(R.string.error_network_not_available);
            throw new NetworkNotAvailableException(msg);
        }
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(Constants.CONNECTION_TIMEOUT);
        // TODO: 10.10.17 java 7 и try с ресурсами. Update 14.10.17 - увы, только с 19 API работает.
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                // TODO: можно было бы по кодам ошибок написать сообщения на русском. Наверное.
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            InputStream is = null;
            String result = null;
            try {
                is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            }finally {
                if (is != null) {
                    is.close();
                }
            }
            return result;
        } finally {
            connection.disconnect();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected Exception getDetailedException(Exception exception, String url) {
        int errorMsgId;
        if (exception instanceof NetworkNotAvailableException) {
            errorMsgId = 0;
        }else if (exception instanceof SocketTimeoutException) {
            errorMsgId = R.string.error_socket_timeout;
        }else if (exception instanceof UnknownHostException) {
            errorMsgId = R.string.error_unknown_host;
        }else if (exception instanceof ConnectException) {
            errorMsgId = R.string.error_connect_exception;
        }else if (exception instanceof JSONException) {
            errorMsgId = R.string.error_json_exception;
        }else if (exception instanceof IOException) {
            errorMsgId = 0;
        }else {
            errorMsgId = R.string.error_other_exception;
        }
        if (errorMsgId != 0) {
            String msg = getContext().getResources().getString(errorMsgId, url);
            return new ExceptionWrapper(exception, msg);
        }
        return exception;
    }

    public static class LoaderResult<T> {
        private final T result;
        private final Exception error;

        public LoaderResult(T result, Exception error) {
            this.result = result;
            this.error = error;
        }

        public T getResult() {
            return result;
        }

        public Exception getError() {
            return error;
        }
    }
}
