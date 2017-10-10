package ru.skypathway.jsontest;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.skypathway.jsontest.data.adapters.MainCardsAdapter;
import ru.skypathway.jsontest.data.adapters.MainCardsAdapter.CardItem;
import ru.skypathway.jsontest.utils.Constants;


/**
 * Created by samsmariya on 10.10.17.
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;

    public MainFragment() {}

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainCardsAdapter cardsAdapter = new MainCardsAdapter(getCardItems());
        mRecyclerView.setAdapter(cardsAdapter);
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

    public List<CardItem> getCardItems() {
        // TODO: 10.10.17 расположить в xml?
        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem(Constants.POSTS, R.string.title_item_posts));
        cardItems.add(new CardItem(Constants.COMMENTS, R.string.title_item_comments));
        cardItems.add(new CardItem(Constants.USERS, R.string.title_item_users));
        cardItems.add(new CardItem(Constants.PHOTOS, R.string.title_item_photos));
        cardItems.add(new CardItem(Constants.TODOS, R.string.title_item_todos));
        return cardItems;
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
