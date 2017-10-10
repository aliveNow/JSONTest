package ru.skypathway.jsontest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.skypathway.jsontest.data.ObjectLoader;
import ru.skypathway.jsontest.data.dao.Post;
import ru.skypathway.jsontest.utils.Constants;


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
    private TextView mTextTitle;
    private TextView mTextPost;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextPost = (TextView) view.findViewById(R.id.text_post);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Constants.Loaders.POSTS, null, this);
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
            return new ObjectLoader<>(this.getActivity(), Constants.CategoryEnum.POSTS, 3);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Post> loader, Post data) {
        mTextTitle.setText(data.getTitle());
        mTextPost.setText(data.getBody());
    }

    @Override
    public void onLoaderReset(Loader<Post> loader) {

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
