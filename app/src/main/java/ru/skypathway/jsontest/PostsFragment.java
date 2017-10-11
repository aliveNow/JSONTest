package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

        View view = inflater.inflate(R.layout.fragment_base_object, container, false);
        LinearLayout placeHolder = (LinearLayout) view.findViewById(R.id.layout_results);
        inflater.inflate(R.layout.content_results_post, placeHolder);
        return view;
    }

    @Override
    protected void onPrepareViews() {
        super.onPrepareViews();
        View view = getView();
        mTextTitle = (TextView) view.findViewById(R.id.text_title);
        mTextPost = (TextView) view.findViewById(R.id.text_post);
        mLayoutEditId.setHint(getString(R.string.hint_enter_post_id, mCategory.maxId));
    }

    @Override
    public @NonNull Constants.CategoryEnum getCategory() {
        return Constants.CategoryEnum.POSTS;
    }

    @Override
    protected void onDataChange(Post data) {
        mTextTitle.setText(data.getTitle());
        mTextPost.setText(data.getBody());
    }

    @Override
    protected int getTitleId() {
        return R.string.title_item_posts;
    }

}
