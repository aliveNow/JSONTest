package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.skypathway.jsontest.data.ArrayLoader;
import ru.skypathway.jsontest.data.BaseLoader.LoaderResult;
import ru.skypathway.jsontest.data.dao.User;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.DividerItemDecoration;


/**
 * Created by samsmariya on 10.10.17.
 */
public class UsersFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<LoaderResult<List<User>>> {
    protected RecyclerView mRecyclerView;
    protected UsersAdapter mUsersAdapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mUsersAdapter = new UsersAdapter(null);
        mRecyclerView.setAdapter(mUsersAdapter);
        getLoaderManager().initLoader(Constants.Loaders.USERS, null, this);
    }

    @Override
    public Loader<LoaderResult<List<User>>> onCreateLoader(int id, Bundle args) {
        return new ArrayLoader<>(getActivity(),
                Constants.CategoryEnum.USERS,
                getResources().getIntArray(R.array.user_ids));
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<List<User>>> loader, LoaderResult<List<User>> data) {
        mUsersAdapter.setUsers(data.getResult());
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<List<User>>> loader) {
        mUsersAdapter.setUsers(null);
    }

    private static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
        protected List<User> mUsers;

        public UsersAdapter(List<User> users) {
            mUsers = users;
        }

        public void setUsers(List<User> users) {
            this.mUsers = users;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user, parent, false);
            return new UsersAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User obj = mUsers.get(position);
            holder.mTextName.setText(obj.getName());
            holder.mTextUsername.setText(obj.getUsername());
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
