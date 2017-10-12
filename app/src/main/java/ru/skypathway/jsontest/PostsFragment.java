package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.skypathway.jsontest.data.dao.Post;
import ru.skypathway.jsontest.utils.Constants;


/**
 * Created by samsmariya on 10.10.17.
 */
public class PostsFragment extends BaseObjectFragment<Post> {

    private TextView mTextTitle;
    private TextView mTextPost;

    public PostsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        return view;
    }

    @Override
    protected void onPrepareViews() {
        super.onPrepareViews();
        mTextTitle = findViewById(R.id.text_title);
        mTextPost = findViewById(R.id.text_post);
    }

    @Override
    public @NonNull Constants.CategoryEnum getCategory() {
        return Constants.CategoryEnum.POSTS;
    }

    @Override
    protected void onDataObjectChange(Post data) {
        if (data != null) {
            mTextTitle.setText(data.getTitle());
            mTextPost.setText(data.getBody());
        }
    }

}
