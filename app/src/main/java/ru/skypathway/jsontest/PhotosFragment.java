package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.skypathway.jsontest.data.dao.Photo;
import ru.skypathway.jsontest.utils.Constants;


/**
 * Created by samsmariya on 11.10.17.
 */
public class PhotosFragment extends BaseObjectFragment<Photo> {

    private TextView mTextTitle;
    private TextView mTextPost;

    public PhotosFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mObjectId = getResources().getInteger(R.integer.photo_id);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        return view;
    }

    @Override
    protected void onPrepareViews() {
        super.onPrepareViews();
        View view = getView();
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextPost = (TextView) view.findViewById(R.id.text_post);
    }

    @Override
    public @NonNull Constants.CategoryEnum getCategory() {
        return Constants.CategoryEnum.PHOTOS;
    }

    @Override
    protected void onDataChange(Photo data) {
        mTextTitle.setText(data.getTitle());
        mTextPost.setText(data.getUrl());
    }

}

