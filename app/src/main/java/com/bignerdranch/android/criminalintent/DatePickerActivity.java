package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by odar on 22/09/17.
 */

public class DatePickerActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DatePickerFragment();
    }
}
