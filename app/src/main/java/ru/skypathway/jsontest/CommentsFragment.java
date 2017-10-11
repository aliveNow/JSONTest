package ru.skypathway.jsontest;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.skypathway.jsontest.data.dao.Comment;
import ru.skypathway.jsontest.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends BaseObjectFragment<Comment> {

    private TextView mTextName;
    private TextView mTextEmail;
    private TextView mTextComment;

    public CommentsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_object, container, false);
        LinearLayout placeHolder = (LinearLayout) view.findViewById(R.id.layout_results);
        inflater.inflate(R.layout.content_results_comment, placeHolder);
        return view;
    }

    @Override
    protected void onPrepareViews() {
        super.onPrepareViews();
        View view = getView();
        mTextName = (TextView) view.findViewById(R.id.text_name);
        mTextEmail = (TextView) view.findViewById(R.id.text_email);
        mTextComment = (TextView) view.findViewById(R.id.text_comment);
        mLayoutEditId.setHint(getString(R.string.hint_enter_comment_id, mCategory.maxId));
    }

    @Override
    public @NonNull Constants.CategoryEnum getCategory() {
        return Constants.CategoryEnum.COMMENTS;
    }

    @Override
    protected void onDataChange(Comment data) {
        mTextName.setText(data.getName());
        mTextEmail.setText(data.getEmail());
        mTextComment.setText(data.getBody());
    }

    @Override
    protected int getTitleId() {
        return R.string.title_item_comments;
    }

}

