package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by odar on 21/09/17.
 */

public class TimePickerFragment extends DialogFragment
                        implements TimePickerDialog.OnTimeSetListener{

    private static final String ARG_TIME = "time";

    public static final String EXTRA_HOUR =
            "com.bibnerdranch.android.criminalintent.hour";
    public static final String EXTRA_MINUTE =
            "com.bibnerdranch.android.criminalintent.minute";

    private TimePicker mTimePicker;
    // Instantiate the calendar with the date to get the hour, minute and seconds.
    final Calendar calendar = Calendar.getInstance();

    public static TimePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(ARG_TIME);

        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Inflate the view for the dialog.
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        sendResult(Activity.RESULT_OK, hourOfDay, minute);
    }

    private void sendResult(int resultCode, int hour, int minute) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_HOUR, hour);
        intent.putExtra(EXTRA_MINUTE, minute);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
