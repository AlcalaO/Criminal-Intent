package com.bignerdranch.android.criminalintent.util;

/**
 * Created by odar on 1/1/18.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
