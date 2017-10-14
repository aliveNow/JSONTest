package ru.skypathway.jsontest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.skypathway.jsontest.data.BaseLoader.LoaderResult;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.data.ExceptionWrapper;
import ru.skypathway.jsontest.utils.InputFilterMinMax;
import ru.skypathway.jsontest.utils.TextChangedListener;
import ru.skypathway.jsontest.utils.Utils;

/**
 * Created by samsmariya on 11.10.17.
 */
public abstract class BaseObjectFragment<T> extends Fragment
        implements LoaderManager.LoaderCallbacks<LoaderResult<T>>,
        View.OnFocusChangeListener,
        TextView.OnEditorActionListener {
    private static final String TAG = BaseObjectFragment.class.getSimpleName();

    protected BaseObjectFragmentDelegate mDelegate;
    protected BaseObjectFragmentListener mListener;

    protected T mObject;
    protected int[] mObjectIds;
    protected final Constants.CategoryEnum mCategory = getCategory();

    protected ExceptionWrapper mLoadingError;

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
        onPrepareViews(savedInstanceState);
        if (savedInstanceState != null) {
            mObjectIds = savedInstanceState.getIntArray(Constants.Extras.OBJECT_IDS);
        }
        if (!Utils.isEmptyArray(mObjectIds)) {
            getLoaderManager().initLoader(getLoaderId(), null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(Constants.Extras.OBJECT_IDS, mObjectIds);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseObjectFragmentDelegate) {
            mDelegate = (BaseObjectFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseObjectFragmentDelegate");
        }
        if (context instanceof BaseObjectFragmentListener) {
            mListener = (BaseObjectFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseObjectFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate = null;
        mListener = null;
    }

    protected void onPrepareViews(Bundle savedInstanceState) {
        mLayoutResults = findViewById(R.id.layout_results);
        Utils.requireNonNull(mLayoutResults, TAG +
                " in view must be layout with id = layout_results");
        mLayoutEnterId = findViewById(R.id.layout_id_enter);
        if (mLayoutEnterId != null) {
            mLayoutEditId = findViewById(R.id.layout_edit_id);
            mEditId = findViewById(R.id.edit_id);
            mButtonConfirmed = findViewById(R.id.button_confirmed);
            mButtonConfirmed.setEnabled(false);
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
            mEditId.setOnEditorActionListener(this);
            mEditId.addTextChangedListener(new TextChangedListener<EditText>(mEditId){
                @Override
                public void onTextChanged(EditText target, Editable s) {
                    mButtonConfirmed.setEnabled(s.length() > 0);
                }
            });
            mEditId.setOnFocusChangeListener(this);
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

    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mListener.onFragmentGetFocus(this, v);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onButtonConfirmedClick();
            return true;
        }
        return false;
    }

    protected<T> T findViewById(@IdRes int viewId) {
        return (T) getView().findViewById(viewId);
    }

    protected int getFirstObjectId() {
        if (!Utils.isEmptyArray(mObjectIds)) {
            return mObjectIds[0];
        }
        return 0;
    }

    public abstract @NonNull Constants.CategoryEnum getCategory();
    protected abstract void onDataObjectChange(T data);

    protected boolean willContinueLoadData(LoaderResult<T> data){
        return false;
    }

    public int getLoaderId() {
        return mCategory.ordinal();
    }
    protected abstract Loader<LoaderResult<T>> getNewLoader(Bundle args);

    @Override
    public Loader<LoaderResult<T>> onCreateLoader(int id, Bundle args) {
        if (id == getLoaderId()) {
            showProgressBar();
            return getNewLoader(args);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<T>> loader, LoaderResult<T> data) {
        mObject = data.getResult();
        if (!mDelegate.onFragmentObjectLoadFinished(this, mObject)) {
            onDataObjectChange(mObject);
        }
        if (data.getError() != null) {
            String errorStr;
            String categoryName = Utils.getCategoryNameGenitive(getActivity(), mCategory, mObjectIds.length);
            errorStr = getResources().getString(R.string.msg_loading_failed,
                    categoryName,
                    getResources().getQuantityString(R.plurals.plurals_ids, mObjectIds.length),
                    Utils.arrayToString(",", mObjectIds));
            mLoadingError = new ExceptionWrapper(data.getError(), errorStr);
            mDelegate.onFragmentObjectLoadingException(this, mLoadingError);
            onLoaderError(mLoadingError);
        }else {
            mLoadingError = null;
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

    protected void onLoaderError(ExceptionWrapper loadingError) {
        mTextError.setText(loadingError.getLocalizedMessage());
        mTextErrorDescription.setText(loadingError.getCauseDescription());
    }

    protected void onButtonConfirmedClick() {
        int objId = 0;
        try {
            objId = Integer.parseInt(mEditId.getText().toString());
        }catch (NumberFormatException nfe) {}
        if (objId > 0) {
            mObjectIds = new int[]{objId};
            Utils.hideSoftInputKeyboard(getActivity());
            mEditId.clearFocus();
            getLoaderManager().restartLoader(getLoaderId(), null, this);
            mListener.onFragmentGetFocus(this, mButtonConfirmed);
        }
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

    public interface BaseObjectFragmentDelegate {
        boolean onFragmentObjectLoadFinished(BaseObjectFragment fragment, Object object);
        boolean onFragmentObjectLoadingException(BaseObjectFragment fragment,
                                                 ExceptionWrapper exception);
    }

    public interface BaseObjectFragmentListener {
        void onFragmentGetFocus(BaseObjectFragment fragment, View view);
    }
}
