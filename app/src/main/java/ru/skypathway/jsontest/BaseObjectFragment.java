package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.skypathway.jsontest.data.BaseLoader.LoaderResult;
import ru.skypathway.jsontest.data.ObjectLoader;
import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.InputFilterMinMax;
import ru.skypathway.jsontest.utils.Utils;

/**
 * Created by samsmariya on 11.10.17.
 */
public abstract class BaseObjectFragment<T extends BaseObject> extends Fragment
        implements LoaderManager.LoaderCallbacks<LoaderResult<T>> {
    private static final String TAG = BaseObjectFragment.class.getSimpleName();

    protected T mObject;
    protected int mObjectId;
    protected final Constants.CategoryEnum mCategory = getCategory();

    protected LoadingError mLoadingError;

    protected View mLayoutResults;
    protected View mLayoutEnterId;
    protected TextInputLayout mLayoutEditId;
    protected EditText mEditId;
    protected Button mButtonConfirmed;
    protected ProgressBar mProgressBar;

    protected View mLayoutError;
    protected TextView mTextErrorDescription;
    protected TextView mTextError;
    protected Button mButtonTryAgain;

    public BaseObjectFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onPrepareViews();
        if (savedInstanceState != null) {
            mObjectId = savedInstanceState.getInt(Constants.Extras.OBJECT_ID);
        }
        if (mObjectId > mCategory.minId) {
            getLoaderManager().initLoader(getLoaderId(), null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Extras.OBJECT_ID, mObjectId);
    }

    protected void onPrepareViews() {
        mLayoutResults = findViewById(R.id.layout_results);
        Utils.requireNonNull(mLayoutResults, TAG +
                " in view must be layout with id = layout_results");
        mLayoutEnterId = findViewById(R.id.layout_id_enter);
        if (mLayoutEnterId != null) {
            mLayoutEditId = findViewById(R.id.layout_edit_id);
            mEditId = findViewById(R.id.edit_id);
            mButtonConfirmed = findViewById(R.id.button_confirmed);

            mButtonConfirmed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonConfirmedClick();
                }
            });
            mLayoutEditId.setHint(getString(R.string.hint_enter_object_id,
                    mCategory.maxId,
                    Utils.getCategoryNameGenitive(getActivity(), mCategory)));
            mEditId.setFilters(new InputFilter[]
                    {new InputFilterMinMax(mCategory.minId, mCategory.maxId)});
            mEditId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onButtonConfirmedClick();
                        return true;
                    }
                    return false;
                }
            });
        }
        mLayoutError = findViewById(R.id.layout_error);
        Utils.requireNonNull(mLayoutError, TAG +
                " in view must be layout with id = layout_error");
        mTextError = findViewById(R.id.text_error);
        mTextErrorDescription = findViewById(R.id.text_error_description);
        mButtonTryAgain = findViewById(R.id.button_try_again);
        mLayoutError.setVisibility(View.GONE);
        mButtonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonTryAgainClick();
            }
        });

        mProgressBar = findViewById(R.id.progress_bar);
        hideProgressBar();
    }

    protected<T> T findViewById(@IdRes int viewId) {
        return (T) getView().findViewById(viewId);
    }

    public abstract @NonNull Constants.CategoryEnum getCategory();
    protected abstract void onDataObjectChange(T data);

    protected boolean willContinueLoadData(LoaderResult<T> data){
        return false;
    }

    public int getLoaderId() {
        return mCategory.ordinal();
    }

    @Override
    public Loader<LoaderResult<T>> onCreateLoader(int id, Bundle args) {
        if (id == getLoaderId()) {
            showProgressBar();
            return new ObjectLoader<>(getActivity(), mCategory, mObjectId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<T>> loader, LoaderResult<T> data) {
        mObject = data.getResult();
        onDataObjectChange(mObject);
        mLoadingError = data.getError() == null ? null : new LoadingError(data.getError());
        if (mLoadingError != null) {
            onLoaderError(mLoadingError);
        }
        if (!willContinueLoadData(data)) {
            hideProgressBar();
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<T>> loader) {
        mObject = null;
        onDataObjectChange(null);
        hideProgressBar();
    }

    protected void onLoaderError(LoadingError loadingError) {
        mTextError.setText(loadingError.getErrorString());
        mTextErrorDescription.setText(loadingError.getErrorDescription());
    }

    protected void onButtonConfirmedClick() {
        Utils.hideSoftInputKeyboard(getActivity());
        mObjectId = Integer.parseInt(mEditId.getText().toString());
        getLoaderManager().restartLoader(getLoaderId(), null, this);
    }

    protected void onButtonTryAgainClick() {
        Utils.hideSoftInputKeyboard(getActivity());
        getLoaderManager().restartLoader(getLoaderId(), null, this);
    }

    protected void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mLayoutResults.setVisibility(View.GONE);
        mLayoutError.setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        mLayoutResults.setVisibility(mObject == null ? View.GONE : View.VISIBLE);
        mLayoutError.setVisibility(mLoadingError == null ? View.GONE : View.VISIBLE);
    }

    protected class LoadingError {
        private String errorDescription;

        LoadingError(Exception exception) {
            errorDescription = exception.getLocalizedMessage();
        }

        public String getErrorString() {
            return getResources().getString(R.string.msg_loading_failed,
                    Utils.getCategoryNameGenitive(getActivity(), mCategory),
                    mObjectId);
        }

        public String getErrorDescription() {
            return errorDescription;
        }

    }
}
