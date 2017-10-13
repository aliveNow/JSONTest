package ru.skypathway.jsontest;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ru.skypathway.jsontest.utils.CanHandleExceptionWrapper;
import ru.skypathway.jsontest.utils.ExceptionWrapper;

/**
 * Created by samsmariya on 10.10.17.
 */

public class MainActivity extends AppCompatActivity
        implements BaseObjectFragment.BaseObjectFragmentDelegate,
        BaseObjectFragment.BaseObjectFragmentListener{
    public static final String TAG = MainActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onFragmentObjectLoadFinished(BaseObjectFragment fragment, Object object) {
        CanHandleExceptionWrapper exceptionHandler = findExceptionHandler(fragment);
        if (exceptionHandler != null) {
            exceptionHandler.hideAllErrors();
        }
        return false;
    }

    @Override
    public boolean onFragmentObjectLoadingException(BaseObjectFragment fragment,
                                                    ExceptionWrapper exception) {
        CanHandleExceptionWrapper exceptionHandler = findExceptionHandler(fragment);
        if (exceptionHandler != null) {
            return exceptionHandler.showError(exception);
        }
        return false;
    }

    private CanHandleExceptionWrapper findExceptionHandler(Fragment fragment) {
        Fragment mainFragment = fragment.getParentFragment();
        if (mainFragment instanceof CanHandleExceptionWrapper) {
            return (CanHandleExceptionWrapper) mainFragment;
        }
        return null;
    }

    @Override
    public void onFragmentGetFocus(BaseObjectFragment fragment, View view) {
        Fragment mainFragment = fragment.getParentFragment();
        if (mainFragment instanceof MainFragment) {
            ((MainFragment)mainFragment).scrollToView(fragment.getView());
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private MainFragment mainFragment = MainFragment.newInstance();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mainFragment;
            }
            return ContactsFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getString(R.string.tab_title_main);
            }else {
                return getString(R.string.tab_contacts_name);
            }
        }
    }
}
