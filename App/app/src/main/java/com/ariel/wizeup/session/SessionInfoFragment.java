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
import com.squareup.picasso.Picasso;

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
    private int LIKE = 1;
    private int DISLIKE = 0;

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

        return view;

    }

    private void initViews(View v) {
        likeCbx = v.findViewById(R.id.like_cbx);
        dislikeCbx = v.findViewById(R.id.dislike_cbx);
        mRatingNum = v.findViewById(R.id.tvRating);
        imageView = v.findViewById(R.id.imageView);
        mSNameTextView = v.findViewById(R.id.tvName);
        mSidTextView = v.findViewById(R.id.tvSid);
        mDateTextView = v.findViewById(R.id.tvDate);
        mTeacherTextView = v.findViewById(R.id.tvTeacher);
        mLocTextView = v.findViewById(R.id.tvLocation);
        mOnlineNum = v.findViewById(R.id.tvOnlineNum);
        mRatingNum.setOnClickListener(view -> openGraph());


    }

    private void openGraph() {
        Intent i = new Intent(getActivity(), GraphActivity.class);
        i.putExtra("sid",sid);
        startActivity(i);
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

        int rating = 0;
        for(int i=0 ; i<_session.getStudents().length; i++){
            if(_session.getStudents()[i].getRating_val().equals("0"))
                rating-=1;
            else if(_session.getStudents()[i].getRating_val().equals("1"))
                rating+=1;
        }

        mRatingNum.setText(String.valueOf(rating));


        String teacher = _session.getTeacher_fname()+" "+_session.getTeacher_lname();
        mTeacherTextView.setText(teacher);
        mLocTextView.setText(_session.getLocation());
        String numStudents = Integer.toString(_session.getStudents().length);
        mOnlineNum.setText(numStudents);

        String pic = _session.getPicID();
        if (pic != null && !(pic.isEmpty()))
            Picasso.with(getActivity())
                    .load(pic)
                    .error(R.drawable.wizeup_logo)
                    .into(imageView);
    }


    @Override
    public void onResume() {
        super.onResume();
        pullSession();
    }


}