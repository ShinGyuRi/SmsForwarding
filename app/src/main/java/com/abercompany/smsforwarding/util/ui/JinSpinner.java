package com.abercompany.smsforwarding.util.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.abercompany.smsforwarding.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JinSpinner extends android.support.v7.widget.AppCompatSpinner {

    private static final String TAG = "JinSpinner";
    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;
    private ArrayAdapter<String> spinnerArrayAdapter;

    public JinSpinner(Context context) {
        super(context);
    }

    public JinSpinner(Context context, int mode) {
        super(context, mode);
    }

    public JinSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JinSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JinSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public interface OnSpinnerEventsListener {

        void onSpinnerOpened();

        void onSpinnerClosed();

    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened();
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed();
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        android.util.Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened() && hasWindowFocus) {
            android.util.Log.i(TAG, "closing popup");
            performClosedEvent();
        }
    }

//    public void initialize(final String[] values) {
//
//        spinnerArrayAdapter = new ArrayAdapter<String>(this.getContext
//                (), R.layout.support_simple_spinner_dropdown_item);
//        List valuesArray = new ArrayList<>(Arrays.asList(values));
//
//        setSpinnerValues(valuesArray);
//        setSpinnerOpenedClosedListener(values);
//    }

    public void setSpinnerValues(List customSpinnerArray) {
        spinnerArrayAdapter.addAll(customSpinnerArray);
    }

    public void setSpinnerOpenedClosedListener(final String[] values) {

        setSelection(getCount() - 1);
        setSpinnerEventsListener(new JinSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                spinnerArrayAdapter.remove(
                        spinnerArrayAdapter.getItem(spinnerArrayAdapter.getCount() - 1));
                setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onSpinnerClosed() {

            }
        });
    }
    //TODO: Make a TextInputLayout hint using CompatLayout
}
