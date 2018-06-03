package com.ariel.wizer.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.ariel.wizer.R;

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

    private Button mBtSetDate;
    private Button mBtCancel;
    private DatePicker mDatePicke;
    OnCallbackReceived mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_date, container, false);
        initViews(view);
        getData();
        return view;
    }

    private void initViews(View v) {
        mDatePicke = (DatePicker) v.findViewById(R.id.datePicker);
        mDatePicke.setMaxDate(new Date().getTime());
        mBtSetDate = (Button) v.findViewById(R.id.button_ok);
        mBtSetDate.setOnClickListener(view -> onTimeSet());
        mBtCancel = (Button) v.findViewById(R.id.button_cancel);
        mBtCancel.setOnClickListener(view -> dismiss());

    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String date = bundle.getString("date");
            try {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date mDate = format.parse(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(mDate);
                mDatePicke.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void onTimeSet() {
        String date = Integer.toString(mDatePicke.getDayOfMonth()) + "/" + Integer.toString(mDatePicke.getMonth())
                + "/" + Integer.toString(mDatePicke.getYear());
        mCallback.Update(date);
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