package com.ariel.wizeup.session;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.robinhood.spark.SparkView;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionInfoFragment extends Fragment {

    private CheckBox likeCbx;
    private CheckBox dislikeCbx;
    private TextView mRatingNum;
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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_info, container, false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scrollView));
        getData();

        initViews(view);

        //        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                classAvgProcess();
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
        mSidTextView = v.findViewById(R.id.tvSid);
        mDateTextView = v.findViewById(R.id.tvDate);
        mTeacherTextView = v.findViewById(R.id.tvTeacher);
        mLocTextView = v.findViewById(R.id.tvLocation);
        mOnlineNum = v.findViewById(R.id.tvOnlineNum);
        mSidTextView.setText(sid);

        sparkView = v.findViewById(R.id.sparkview);
        scrubInfoTextView = v.findViewById(R.id.scrub_info_textview);
        }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
            sid = "1234";////////////rm
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



    @Override
    public void onResume() {
        super.onResume();
    }


}