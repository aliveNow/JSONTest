package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ru.skypathway.jsontest.data.BaseLoader.LoaderResult;
import ru.skypathway.jsontest.data.ObjectLoader;
import ru.skypathway.jsontest.data.dao.Photo;
import ru.skypathway.jsontest.utils.Constants;


/**
 * Created by samsmariya on 11.10.17.
 */
public class PhotosFragment extends BaseObjectFragment<Photo>
        implements Callback {

    private TextView mTextTitle;
    private ImageView mImageView;

    public PhotosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        return view;
    }

    @Override
    protected void onPrepareViews(Bundle savedInstanceState) {
        super.onPrepareViews(savedInstanceState);
        View view = getView();
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mImageView = (ImageView) view.findViewById(R.id.image_view);
        if (savedInstanceState == null) {
            mObjectIds = new int[]{getResources().getInteger(R.integer.photo_id)};
        }
    }

    @Override
    public @NonNull Constants.CategoryEnum getCategory() {
        return Constants.CategoryEnum.PHOTOS;
    }

    @Override
    protected void onDataObjectChange(Photo data) {
        if (data != null) {
            mTextTitle.setText(data.getTitle());
        }
    }

    @Override
    protected boolean willContinueLoadData(LoaderResult<Photo> data) {
        Photo result = data.getResult();
        if (result != null && result.getUrl() != null) {
            Picasso.with(getActivity())
                    .load(result.getUrl())
                    .into(mImageView, this);
            return true;
        }
        return false;
    }

    @Override
    protected Loader<LoaderResult<Photo>> getNewLoader(Bundle args) {
        return new ObjectLoader<>(getActivity(), mCategory, getFirstObjectId());
    }

    @Override
    public void onSuccess() {
        mImageView.setVisibility(View.VISIBLE);
        hideProgressBar();
    }

    @Override
    public void onError() {
        mImageView.setVisibility(View.GONE);
        hideProgressBar();
    }
}

