package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by odar on 08/10/17.
 */

public class PhotoZoomedFragment extends DialogFragment {

    private ImageView mZoomedPhotoView;

    private static final String ARG_PATH = "path";

    public static PhotoZoomedFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);

        PhotoZoomedFragment fragment = new PhotoZoomedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String path = getArguments().getString(ARG_PATH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        Bitmap zoomed = PictureUtils.getScaledBitmap(path, getActivity());

        mZoomedPhotoView = (ImageView) v.findViewById(R.id.zoomed_photo);
        mZoomedPhotoView.setImageBitmap(zoomed);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.photo_zoomed_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
