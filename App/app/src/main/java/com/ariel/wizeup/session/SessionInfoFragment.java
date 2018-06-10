package com.ariel.wizeup.session;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.robinhood.spark.SparkView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionInfoFragment extends Fragment {

    private CheckBox likeCbx;
    private CheckBox dislikeCbx;
    private TextView mRatingNum;

    private TextView mSNameTextView;
    private TextView mSidTextView;
    private TextView mDateTextView;
    private TextView mTeacherTextView;
    private TextView mLocTextView;
    private TextView mOnlineNum;

    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String sid;
    private int LIKE = 1;
    private int DISLIKE = 0;

    private SparkView sparkView;
    private RandomizedAdapter adapter;
    private TextView scrubInfoTextView;
//    private int delay = 5000;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_info, container, false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scrollView));
        getData();
        initViews(view);
        pullSession();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                pullSession();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);


        likeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentRating = Integer.parseInt(mRatingNum.getText().toString().trim());
            if (isChecked) {
                if (dislikeCbx.isChecked()) {
                    dislikeCbx.setChecked(false);
                    mRatingNum.setText(String.valueOf(currentRating + 2));
                } else
                    mRatingNum.setText(String.valueOf(currentRating + 1));
            } else
                mRatingNum.setText(String.valueOf(currentRating - 1));
            tryChangeVal(LIKE);
        });

        dislikeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentRating = Integer.parseInt(mRatingNum.getText().toString().trim());
            if (isChecked) {
                if (likeCbx.isChecked()) {
                    likeCbx.setChecked(false);
                    mRatingNum.setText(String.valueOf(currentRating - 2));
                } else
                    mRatingNum.setText(String.valueOf(currentRating - 1));
            } else
                mRatingNum.setText(String.valueOf(currentRating + 1));
            tryChangeVal(DISLIKE);
        });

        adapter = new RandomizedAdapter();
        sparkView.setAdapter(adapter);
        sparkView.setScrubListener(value -> {
            if (value == null) {
                scrubInfoTextView.setText(R.string.scrub_empty);
            } else {
                scrubInfoTextView.setText(getString(R.string.scrub_format, value));
            }
        });


        return view;

    }

    private void initViews(View v) {
        likeCbx = v.findViewById(R.id.like_cbx);
        dislikeCbx = v.findViewById(R.id.dislike_cbx);
        mRatingNum = v.findViewById(R.id.tvRating);

        mSNameTextView = v.findViewById(R.id.tvName);
        mSidTextView = v.findViewById(R.id.tvSid);
        mDateTextView = v.findViewById(R.id.tvDate);
        mTeacherTextView = v.findViewById(R.id.tvTeacher);
        mLocTextView = v.findViewById(R.id.tvLocation);
        mOnlineNum = v.findViewById(R.id.tvOnlineNum);

        sparkView = v.findViewById(R.id.sparkview);
        scrubInfoTextView = v.findViewById(R.id.scrub_info_textview);
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
        }
    }

    private void tryChangeVal(int val) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(sid, val)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseChangeVal, i -> mServerResponse.handleError(i)));
    }


    private void handleResponseChangeVal(Response response) {
    }


    private void pullSession() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getSessionById(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePullSession, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePullSession(Session _session) {
        mSNameTextView.setText(_session.getName());
        mSidTextView.setText(_session.getSid());

        //Date
        Date date = _session.getCreation_date();
        if (date != null) {
            Format formatter = new SimpleDateFormat("EEE, d MMM yyyy");
            String s = formatter.format(date);
            mDateTextView.setText(s);
        }


        String teacher = _session.getTeacher_fname()+" "+_session.getTeacher_lname();
        mTeacherTextView.setText(teacher);
        mLocTextView.setText(_session.getLocation());
        String numStudents = Integer.toString(_session.getStudents().length);
        mOnlineNum.setText(numStudents);
    }


    @Override
    public void onResume() {
        super.onResume();
        pullSession();
    }


}