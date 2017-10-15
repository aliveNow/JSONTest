package ru.skypathway.jsontest.data;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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
import java.util.concurrent.TimeUnit;

import ru.skypathway.jsontest.R;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.Utils;

/**
 * Created by samsmariya on 10.10.17.
 *
 * Базовый загрузчик объектов.
 * Загружает все объекты с id = {@link #mObjectIds}. Возвращает результат {@link LoaderResult},
 * который при успешной загрузке в {@link LoaderResult#getResult()} содержит {@link List} объектов,
 * расположенных в том же порядке, что и их id в {@link #mObjectIds}.
 * Если хотя бы один объект загрузить не удалось, {@link LoaderResult#getResult()} будет возвращать
 * {@code null}, а {@link LoaderResult#getError()} будет содержать ошибку загрузки.
 *
 * Пример реализации частично подсмотрен отсюда:
 * https://developer.android.com/reference/android/content/AsyncTaskLoader.html
 */

public abstract class BaseLoader<D> extends AsyncTaskLoader<BaseLoader.LoaderResult<D>> {
    private static final String TAG = BaseLoader.class.getSimpleName();

    protected int[] mObjectIds;

    protected Cache<Integer, D> mCache;
    private int mCacheSize = 50;
    private int mCacheExpireTime = 10;

    public BaseLoader(@NonNull Context context,
                      @NonNull int[] objectIds) {
        super(context);
        Utils.requireNonNull(objectIds, TAG + ": ObjectIds can't be null");
        mObjectIds = objectIds;
        createCache();
    }

    //region Overridden Loader Methods
    //--------------------------------------------------------------------------------
    @Override
    public LoaderResult<D> loadInBackground() {
        LoaderResult<D> loaderResult;
        String url = null;
        try {
            // FIXME: 14.10.17 sleep поставлен, чтобы отследить работу progressbar
            SystemClock.sleep(1000);
            List<D> resultList = new ArrayList<>();
            for (int objectId : mObjectIds) {
                // TODO: 14.10.17  реализовать проверку, была ли отменена загрузка
                D resultObject = mCache.getIfPresent(objectId);
                if (resultObject == null) {
                    url = getLoadingUri(objectId).toString();
                    String string = getUrlString(url);
                    if (string != null) {
                        resultObject = convertToResult(objectId, string);
                        mCache.put(objectId, resultObject);
                    }else {
                        throw new IOException("Result is NULL at url: " + url);
                    }
                }
                resultList.add(resultObject);
            }
            loaderResult = new LoaderResult<>(resultList, null);
        }catch (Exception exception) {
            Log.e(TAG, "Failed to load: " + url, exception);
            loaderResult = new LoaderResult<>(getDetailedException(exception, url));
        }
        return loaderResult;
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        List<D> results = getCacheResult(mObjectIds);

        if (results != null) {
            deliverResult(new LoaderResult<>(results, null));
        }

        if (takeContentChanged() || results == null) {
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
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        releaseCache();
    }
    //--------------------------------------------------------------------------------
    //endregion

    //region Abstract Methods
    //--------------------------------------------------------------------------------
    /**
     * Возвращает URI для загрузки объекта с id = {@code objectId}.
     *
     * @param objectId идентификатор загружаемого объекта
     * @return Uri для загрузки объекта с id = {@code objectId}
     * @throws Exception любое исключение при невозможности определить URI загрузки.
     * Например: "такого id не существует", если это можно проверить заранее.
     */
    protected abstract @NonNull Uri getLoadingUri(int objectId) throws Exception;

    /**
     * Метод парсинга строки, загруженной по {@link #getLoadingUri(int)}, в объект.
     *
     * @param objectId идентификатор загруженного объекта
     * @param string строка для парсинга
     * @return объект, соответствующий параметризованному типу
     * @throws Exception любое исключение, возникшее при парсинге
     */
    protected abstract @NonNull D convertToResult(int objectId,
                                                  @NonNull String string) throws Exception;
    //--------------------------------------------------------------------------------
    //endregion

    //region Cache
    //--------------------------------------------------------------------------------
    /* TODO: 15.10.17 кэш тут на самом деле пока бесполезен, потому что каждый раз, когда
    меняется id, вызывается restartLoader и создаётся новый загрузчик.
    Подумать, что можно сделать.
     */
    public synchronized Cache<Integer, D> createCache() {
        mCache = CacheBuilder.newBuilder()
                .maximumSize(mCacheSize)
                .expireAfterWrite(mCacheExpireTime, TimeUnit.MINUTES)
                .build();
        return mCache;
    }

    protected List<D> getCacheResult(int[] ids) {
        if (ids == null) {
            return null;
        }
        List<D> results = new ArrayList<>();
        for (int id : ids) {
            D obj = mCache.getIfPresent(id);
            if (obj != null) {
                results.add(obj);
            }else {
                return null;
            }
        }
        return null;
    }

    protected synchronized void releaseCache() {
        if (mCache != null) {
            mCache.invalidateAll();
            mCache.cleanUp();
        }
        mCache = null;
    }
    //--------------------------------------------------------------------------------
    //endregion

    //region Loading
    //--------------------------------------------------------------------------------
    protected String getUrlString(String urlSpec) throws IOException {
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
    //--------------------------------------------------------------------------------
    //endregion

    //region Helper Methods
    //--------------------------------------------------------------------------------
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected Exception getDetailedException(Exception exception, String url) {
        int errorMsgId;
        if (exception instanceof NetworkNotAvailableException
                || exception instanceof ExceptionWrapper) {
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
    //--------------------------------------------------------------------------------
    //endregion

    public static class LoaderResult<T> {
        private final List<T> mResult;
        private final Exception mError;

        public LoaderResult(List<T> result, Exception error) {
            mResult = result;
            mError = error;
        }

        public LoaderResult(Exception error) {
            mResult = null;
            mError = error;
        }

        public List<T> getResult() {
            return mResult;
        }

        public Exception getError() {
            return mError;
        }
    }
}
