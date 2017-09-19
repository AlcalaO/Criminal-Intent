package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

/**
 * Created by odar on 08/09/17.
 */

public class CrimePagerActivity extends AppCompatActivity{

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mGoToFirst;
    private Button mGoToLast;

    private static final String EXTRA_CRIME_ID =
            "com.bignerdranch.android.criminalintent.crime_id";

    // This intent is for create a new intent for this activity, its static to be called outside
    // the class. This works with fragment arguments, the argument here is the crime ID.
    public static Intent newIntent (Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    // onCreate method will wire up the view for the activity and initialize components
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);


        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        // Convert pixels to dp to set the margin between pages
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                16, r.getDisplayMetrics());

        // Set the padding for the actual page
        mViewPager.setPageMargin((int)px);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(16, 16, 16, 16);

        // Add a onPageChangeListener to the ViewPager
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mViewPager.getCurrentItem() == 0){
                    mGoToFirst.setEnabled(false);
                } else {
                    mGoToFirst.setEnabled(true);
                }
                
                if (mViewPager.getCurrentItem() == mCrimes.size() - 1){
                    mGoToLast.setEnabled(false);
                } else {
                    mGoToLast.setEnabled(true);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mGoToFirst = (Button) findViewById(R.id.go_to_first);
        mGoToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mGoToLast = (Button) findViewById(R.id.go_to_last);
        mGoToLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });


        FragmentManager fragmentManager = getSupportFragmentManager();
        // FragmentStatePagerAdapter needs a FragmentManager to function
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        /* This is to find the crime it was asked for instead of the first element on the
        * array */
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
