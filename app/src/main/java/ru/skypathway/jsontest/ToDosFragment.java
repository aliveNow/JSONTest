package ru.skypathway.jsontest;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

import ru.skypathway.jsontest.data.dao.ToDo;
import ru.skypathway.jsontest.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class ToDosFragment extends BaseObjectFragment<ToDo> {
    private TextView mTextId;
    private TextView mTextTitle;
    private TextView mTextCompleted;

    public ToDosFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_dos, container, false);
    }

    @Override
    protected void onPrepareViews(Bundle savedInstanceState) {
        super.onPrepareViews(savedInstanceState);
        mTextId = findViewById(R.id.text_id);
        mTextTitle = findViewById(R.id.text_title);
        mTextCompleted = findViewById(R.id.text_completed);
        if (savedInstanceState == null) {
            Random r = new Random();
            int randomId = r.nextInt(mCategory.maxId + 1 - mCategory.minId) + mCategory.minId;
            mObjectIds = new int[]{randomId};
        }
    }

    @NonNull
    @Override
    public Constants.CategoryEnum getCategory() {
        return Constants.CategoryEnum.TODOS;
    }

    @Override
    protected boolean isOneObjectFragment() {
        return true;
    }

    @Override
    protected void onDataObjectChange(ToDo data) {
        if (data != null) {
            mTextId.setText(getString(R.string.text_id_equals, data.getId()));
            mTextTitle.setText(data.getTitle());
            mTextCompleted.setText(data.isCompleted()
                    ? getString(R.string.text_status_completed)
                    : getString(R.string.text_status_non_completed));
        }
    }

}
