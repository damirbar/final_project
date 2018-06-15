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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.settings.FeedbackActivity;
import com.ariel.wizeup.utils.Constants;
import com.mindorks.placeholderview.annotations.expand.Toggle;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionInfoFragment extends Fragment {

    private RadioButton mRadioDontUnderstand;
    private RadioButton mRadioUnderstand;
    private RadioGroup toggle;
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
    private Handler handler;
    private String email;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_info, container, false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scrollView));
        handler = new Handler();
        getData();
        initSharedPreferences();
        initViews(view);

        toggle.setOnCheckedChangeListener((group, checkedId) -> {
            if(mRadioUnderstand.isChecked()) {
                tryChangeVal(LIKE);
                pullSession();

            } else if(mRadioDontUnderstand.isChecked()) {
                tryChangeVal(DISLIKE);
                pullSession();
            }
        });

        return view;

    }



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pullSession();
            int delay = 5000;
            handler.postDelayed(this, delay);
        }
    };


    private void initViews(View v) {
        toggle = v.findViewById(R.id.toggle);
        mRadioUnderstand = v.findViewById(R.id.radio_1);
        mRadioDontUnderstand = v.findViewById(R.id.radio_2);
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
        email = mRetrofitRequests.getSharedPreferences().getString(Constants.EMAIL, "");
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

        if (_session.getAdmin().equalsIgnoreCase(email)) {
            toggle.setVisibility(View.GONE);
        }
        else{
//            for (int i = 0; i < _session.getLikers().length; i++) {
//                if (_session.getLikers()[i].equalsIgnoreCase(email)) {
//                    mRadioUnderstand.setChecked(true);
//                }
//            }
//            ////temp
//
//            for (int i = 0; i < _session.getDislikers().length; i++) {
//                if (_session.getDislikers()[i].equalsIgnoreCase(email)) {
//                    mRadioDontUnderstand.setChecked(true);
//                }
//            }
        }

        int all = _session.getLikers().length + _session.getDislikers().length;
        if(all == 0){
            all = 1;
        }
        double Likers = _session.getLikers().length;
        double dislikers = _session.getDislikers().length;

        double result = (Likers / all) * 100;
        double result2 = (dislikers / all) * 100;


        //Date
        Date date = _session.getCreation_date();
        if (date != null) {
            Format formatter = new SimpleDateFormat("EEE, d MMM yyyy");
            String s = formatter.format(date);
            mDateTextView.setText(s);
        }

        mRatingNum.setText(String.valueOf((int) result) + "%");
        mRatingNum2.setText(String.valueOf((int) result2) + "%");

        mSNameTextView.setText(_session.getName());
        mSidTextView.setText(_session.getSid());
        String teacher = _session.getTeacher_fname() + " " + _session.getTeacher_lname();
        mTeacherTextView.setText(teacher);
        mLocTextView.setText(_session.getLocation());
        String numStudentsStr = Integer.toString(_session.getStudents().length);
        mOnlineNum.setText(numStudentsStr);

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