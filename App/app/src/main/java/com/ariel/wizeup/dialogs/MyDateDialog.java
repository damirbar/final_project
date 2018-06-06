package com.ariel.wizeup.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.ariel.wizeup.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MyDateDialog extends DialogFragment {

    public interface OnCallbackReceived {
        public void Update(String date);
    }

    public static final String TAG = MyDateDialog.class.getSimpleName();

    private DatePicker mDatePicker;
    OnCallbackReceived mCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date, container, false);
        initViews(view);
        getData();
        return view;
    }

    private void initViews(View v) {
        mDatePicker = v.findViewById(R.id.datePicker);
        mDatePicker.setMaxDate(new Date().getTime());
        Button mBtSetDate = v.findViewById(R.id.button_ok);
        mBtSetDate.setOnClickListener(view -> onTimeSet());
        Button mBtCancel = v.findViewById(R.id.button_cancel);
        mBtCancel.setOnClickListener(view -> dismiss());

    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String date = bundle.getString("date");
            try {
                DateFormat format = new SimpleDateFormat("d MMM yyyy");
                Date mDate = format.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(mDate);
                mDatePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void onTimeSet() {
        int   day  = mDatePicker.getDayOfMonth();
        int   month= mDatePicker.getMonth();
        int   year = mDatePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");
        String formatedDate = sdf.format(calendar.getTime());
        mCallback.Update(formatedDate);
        dismiss();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCallbackReceived) context;
        } catch (ClassCastException e) {}
    }
}