package com.ariel.wizeup.session;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.settings.FeedbackActivity;
import com.ariel.wizeup.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionInfoFragment extends Fragment {

    private CheckBox likeCbx;
    private CheckBox dislikeCbx;
    private TextView mRatingNum;
    private TextView mRatingNum2;

    private ImageView imageView;

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
    private String LIKE = "1";
    private String DISLIKE = "0";
    private double understand;
    private double dontUnderstand;
    private double numStudents;


    private Handler handler;
    private String email;
    private int delay = 5000;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_info, container, false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scrollView));
        getData();
        initViews(view);
        initSharedPreferences();
        handler = new Handler();


        likeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            double result = 0;
//            double result2 = 0;
            if (isChecked) {
                if (dislikeCbx.isChecked()) {
                    dislikeCbx.setChecked(false);
//                    dontUnderstand -= 1;
//                    result2 = (dontUnderstand / numStudents) * 100;
//                    mRatingNum2.setText(String.valueOf((int)result2) + "%");

                }
//                understand += 1;
            } else {
//                understand -= 1;
            }
//            result = (understand / numStudents) * 100;
//            mRatingNum.setText(String.valueOf((int)result) + "%");
            tryChangeVal(LIKE);

        });

        dislikeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            double result = 0;
//            double result2 = 0;
            if (isChecked) {
                if (likeCbx.isChecked()) {
                    likeCbx.setChecked(false);
//                    understand -= 1;
//                    result = (understand / numStudents) * 100;
//                    mRatingNum.setText(String.valueOf((int)result) + "%");
                }
//                dontUnderstand += 1;

            } else {
//                dontUnderstand -= 1;

            }
//            result2 = (dontUnderstand / numStudents) * 100;
//            mRatingNum2.setText(String.valueOf((int)result2) + "%");
            tryChangeVal(DISLIKE);
        });

        return view;

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pullSession();
            handler.postDelayed(this, delay);
        }
    };


    private void initViews(View v) {
        likeCbx = v.findViewById(R.id.like_cbx);
        dislikeCbx = v.findViewById(R.id.dislike_cbx);
        mRatingNum = v.findViewById(R.id.tvRating);
        mRatingNum2 = v.findViewById(R.id.tvRating2);

        imageView = v.findViewById(R.id.imageView);
        mSNameTextView = v.findViewById(R.id.tvName);
        mSidTextView = v.findViewById(R.id.tvSid);
        mDateTextView = v.findViewById(R.id.tvDate);
        mTeacherTextView = v.findViewById(R.id.tvTeacher);
        mLocTextView = v.findViewById(R.id.tvLocation);
        mOnlineNum = v.findViewById(R.id.tvOnlineNum);
        mRatingNum.setOnClickListener(view -> openGraph());


    }

    private void initSharedPreferences() {
        email = mRetrofitRequests.getSharedPreferences().getString(Constants.TYPE, "");
    }


    private void openGraph() {
        Intent i = new Intent(getActivity(), GraphActivity.class);
        i.putExtra("sid", sid);
        startActivity(i);
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
        }
    }

    private void tryChangeVal(String val) {
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

        dontUnderstand = 0;
        understand = 0;
        numStudents = 1;
        double result = 0;
        double result2 = 0;
        if (_session.getStudents().length != 0) {
            numStudents = _session.getStudents().length;
            for (int i = 0; i < numStudents; i++) {

                if (_session.getStudents()[i].getRating_val().equals("1")) {
                    understand += 1;
                    if (_session.getStudents()[i].getEmail().equalsIgnoreCase(email)) {
                        likeCbx.setChecked(true);
                    }
                } else {
                    dontUnderstand += 1;
                    if (_session.getStudents()[i].getEmail().equalsIgnoreCase(email)) {
                        dislikeCbx.setChecked(true);
                    }
                }
            }
        }
        result = (understand / numStudents) * 100;
        result2 = (dontUnderstand / numStudents) * 100;
        mRatingNum.setText(String.valueOf((int)result) + "%");
        mRatingNum2.setText(String.valueOf((int)result2) + "%");


        String teacher = _session.getTeacher_fname() + " " + _session.getTeacher_lname();
        mTeacherTextView.setText(teacher);
        mLocTextView.setText(_session.getLocation());
        String numStudents = Integer.toString(_session.getStudents().length);
        mOnlineNum.setText(numStudents);

//        String pic = _session.getPicID();
//        if (pic != null && !(pic.isEmpty()))
//            Picasso.with(getActivity())
//                    .load(pic)
//                    .error(R.drawable.wizeup_logo)
//                    .into(imageView);
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 0);
    }


}