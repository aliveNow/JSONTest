package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
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

import ru.skypathway.jsontest.data.ObjectLoader;
import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.InputFilterMinMax;
import ru.skypathway.jsontest.utils.Utils;

/**
 * Created by samsmariya on 11.10.17.
 */

public abstract class BaseObjectFragment<T extends BaseObject> extends Fragment
        implements LoaderManager.LoaderCallbacks<T> {
    protected T mObject;
    protected int mObjectId;
    protected final Constants.CategoryEnum mCategory = getCategory();

    protected View mLayoutResults;
    protected View mLayoutEnterId;
    protected TextInputLayout mLayoutEditId;
    protected EditText mEditId;
    protected Button mButtonConfirmed;
    protected ProgressBar mProgressBar;

    public BaseObjectFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onPrepareViews();
        hideProgressBar();
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
        View view = getView();
        mLayoutResults = view.findViewById(R.id.layout_results);
        mLayoutEnterId = view.findViewById(R.id.layout_id_enter);
        mLayoutEditId = (TextInputLayout) view.findViewById(R.id.layout_edit_id);
        mEditId = (EditText) view.findViewById(R.id.edit_id);
        mButtonConfirmed = (Button) view.findViewById(R.id.button_confirmed);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        TextView textCardTitle = (TextView) view.findViewById(R.id.text_card_title);
        textCardTitle.setText(getTitleId());

        mButtonConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonConfirmedClick();
            }
        });
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

    public abstract @NonNull Constants.CategoryEnum getCategory();
    protected abstract void onDataChange(T data);
    protected abstract @StringRes int getTitleId();

    public int getLoaderId() {
        return mCategory.ordinal();
    }

    @Override
    public Loader<T> onCreateLoader(int id, Bundle args) {
        if (id == getLoaderId()) {
            showProgressBar();
            return new ObjectLoader<>(getActivity(), mCategory, mObjectId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {
        mObject = data;
        onDataChange(data);
        hideProgressBar();
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {
        mObject = null;
        onDataChange(null);
        hideProgressBar();
    }

    protected void onButtonConfirmedClick() {
        Utils.hideSoftInputKeyboard(getActivity());
        mObjectId = Integer.parseInt(mEditId.getText().toString());
        getLoaderManager().restartLoader(getLoaderId(), null, this);
    }

    protected void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLayoutResults.setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mLayoutResults.setVisibility(mObject == null ? View.GONE : View.VISIBLE);
    }
}