package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


/**
 * Created by samsmariya on 10.10.17.
 */
public class UsersFragment extends Fragment {
    protected RecyclerView mRecyclerView;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] users = { "Иван", "Марья", "Петр", "Антон", "Даша", "Иван", "Марья", "Петр", "Антон", "Даша" };
        UsersAdapter adapter = new UsersAdapter(Arrays.asList(users));
        mRecyclerView.setAdapter(adapter);
    }

    private static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
        protected List<String> mUsers;

        public UsersAdapter(List<String> users) {
            mUsers = users;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UsersAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String obj = mUsers.get(position);
            holder.mTextName.setText(obj);
            holder.mTextUsername.setText(obj);
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextName;
            TextView mTextUsername;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextName = (TextView) itemView.findViewById(R.id.text_name);
                mTextUsername = (TextView) itemView.findViewById(R.id.text_username);
            }
        }
    }

}
