package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.skypathway.jsontest.utils.CanHandleExceptionWrapper;
import ru.skypathway.jsontest.data.ExceptionWrapper;
import ru.skypathway.jsontest.data.NetworkNotAvailableException;


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
public class MainFragment extends Fragment
        implements CanHandleExceptionWrapper {

    private NestedScrollView mScrollView;
    private View mCardError;

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
        mCardError = view.findViewById(R.id.card_error);
        mScrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        hideAllErrors();
        return view;
    }

    @Override
    public boolean showError(ExceptionWrapper exception) {
        if (exception.getCause() instanceof NetworkNotAvailableException) {
            mCardError.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public void hideError(ExceptionWrapper exception) {
        hideAllErrors();
    }

    @Override
    public void hideAllErrors() {
        mCardError.setVisibility(View.GONE);
    }

    public void scrollToView(View view) {
        mScrollView.smoothScrollTo(0, view.getTop());
    }

}
