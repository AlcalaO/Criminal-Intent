package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.*;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));
//                startActivity(i);
                // Using ShareCompat.IntentBuilder to make the implicit intent
                String type = "text/plain";
                String title = "Send report";
                ShareCompat.IntentBuilder ib = ShareCompat.IntentBuilder.from(getActivity());
                Intent i = ib.setType(type)
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(title)
                        .createChooserIntent();
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, REQUEST_CONTACT);
                }
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Guarding against no contacts app
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCallSuspectButton = (Button) v.findViewById(R.id.call_crime_suspect);
        mCallSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactNumber = "";
                List<String> numbers = new ArrayList<String>();
                String contactID = "";
                String contactName = "";
                // ContentResolver retrieves data about contacts.
                ContentResolver cr = getActivity().getContentResolver();

                Cursor curId = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if (curId.getCount() > 0) {
                    while (curId.moveToNext()) {
                        contactID = curId.getString(
                                curId.getColumnIndex(ContactsContract.Contacts._ID));
                        contactName = curId.getString(
                                curId.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        if (Integer.parseInt(curId.getString(
                                curId.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor curPhone = cr.query(Phone.CONTENT_URI, null,
                                    Phone.CONTACT_ID + " = ?",
                                    new String[] {contactID}, null);

                            while (curPhone.moveToNext()) {
                                contactNumber =  curPhone.getString(
                                        curPhone.getColumnIndex(Phone.NUMBER));
                                numbers.add(contactNumber);
                            }
                        }
                    }
                }
//
//                String [] projectionID = new String[] {
//                        ContactsContract.Contacts._ID,
//                        ContactsContract.Contacts.DISPLAY_NAME
//                };
//
//                Cursor cursorId = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                        projectionID, "DISPLAY_NAME = '" + mCrime.getSuspect() + "'", null, null);
//
//                if (cursorId == null) {
//                    return;
//                } else if (cursorId.getCount() >= 1) {
//                    contactID = cursorId.getString(
//                            cursorId.getColumnIndex(ContactsContract.Contacts._ID));
//                    cursorId.close();
//                }
//
//                // projection works as the argument to query the contact, in this case we'll
//                // look for the _ID of the contact.
//                String[] projectionPhone = new String[] {
//                        Phone._ID,
//                        Phone.NUMBER
//                };
//                // Here we query the contact from the ContactsContract.Contacts database
//                Cursor cursorPhone = cr.query(ContactsContract.Contacts.CONTENT_LOOKUP_URI,
//                        projectionPhone, null, null, null);
//
//
//                if (cursorPhone == null) {
//                } else if (cursorPhone.getCount() >= 1) {
//                    contactNumber = cursorPhone.getString(
//                            cursorPhone.getColumnIndex(Phone.NUMBER));
//                    cursorPhone.close();
//                }
//                for (int i = 0; i < numbers.size(); i++) {
//
//                }
                Uri number = Uri.parse("tel:"+contactNumber);
                Intent i = new Intent(Intent.ACTION_DIAL, number);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return values for
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            // Perform your query - the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null
                                                            , null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data
                // That is you suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(),
                dateString, solvedString, suspect);

        return report;
    }
}
