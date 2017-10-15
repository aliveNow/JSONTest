package ru.skypathway.jsontest;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.skypathway.jsontest.data.BaseObjectType;
import ru.skypathway.jsontest.data.dao.Comment;


/**
 * Created by samsmariya on 11.10.17.
 */
public class CommentsFragment extends BaseObjectFragment<Comment> {

    private TextView mTextName;
    private TextView mTextEmail;
    private TextView mTextComment;

    public CommentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        return view;
    }

    @Override
    protected void onPrepareViews(Bundle savedInstanceState) {
        super.onPrepareViews(savedInstanceState);
        mTextName = findViewById(R.id.text_name);
        mTextEmail = findViewById(R.id.text_email);
        mTextComment = findViewById(R.id.text_comment);
    }

    @Override
    public @NonNull
    BaseObjectType getBaseObjectType() {
        return BaseObjectType.COMMENTS;
    }

    @Override
    protected boolean isOneObjectFragment() {
        return true;
    }

    @Override
    protected void onDataObjectChange(Comment data) {
        if (data != null) {
            mTextName.setText(data.getName());
            mTextEmail.setText(data.getEmail());
            mTextComment.setText(data.getBody());
        }
    }

}

