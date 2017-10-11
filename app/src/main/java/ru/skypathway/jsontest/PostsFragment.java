package ru.skypathway.jsontest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.skypathway.jsontest.data.ObjectLoader;
import ru.skypathway.jsontest.data.dao.Post;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.Utils;


/**
 * Created by samsmariya on 10.10.17.
 *
 * Activities that contain this fragment must implement the
 * {@link PostsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PostsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Post> {
    private OnFragmentInteractionListener mListener;
    protected Post mObject;
    protected int mObjectId;
    protected int mLoaderId = Constants.Loaders.POSTS;

    protected View mLayoutResults;
    private TextView mTextTitle;
    private TextView mTextPost;
    protected View mLayoutEnterId;
    protected EditText mEditId;
    protected Button mButtonConfirmed;
    protected ProgressBar mProgressBar;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        mLayoutResults = view.findViewById(R.id.layout_results);
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextPost = (TextView) view.findViewById(R.id.text_post);
        mLayoutEnterId = view.findViewById(R.id.layout_id_enter);
        mEditId = (EditText) view.findViewById(R.id.edit_id);
        mButtonConfirmed = (Button) view.findViewById(R.id.button_confirmed);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hideProgressBar();
        if (savedInstanceState != null) {
            mObjectId = savedInstanceState.getInt(Constants.Extras.OBJECT_ID);
        }
        if (mObjectId != 0) {
            getLoaderManager().initLoader(mLoaderId, null, this);
        }
        mButtonConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonConfirmedClick();
            }
        });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Extras.OBJECT_ID, mObjectId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Post> onCreateLoader(int id, Bundle args) {
        if (id == Constants.Loaders.POSTS) {
            showProgressBar();
            return new ObjectLoader<>(this.getActivity(), Constants.CategoryEnum.POSTS, mObjectId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Post> loader, Post data) {
        mObject = data;
        mTextTitle.setText(data.getTitle());
        mTextPost.setText(data.getBody());
        hideProgressBar();
    }

    @Override
    public void onLoaderReset(Loader<Post> loader) {
        mObject = null;
        hideProgressBar();
    }

    protected void onButtonConfirmedClick() {
        Utils.hideSoftInputKeyboard(getActivity());
        mObjectId = Integer.parseInt(mEditId.getText().toString());
        getLoaderManager().restartLoader(mLoaderId, null, this);
    }

    protected void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLayoutResults.setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mLayoutResults.setVisibility(mObject == null ? View.GONE : View.VISIBLE);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
