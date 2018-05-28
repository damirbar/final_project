package com.ariel.wizer.session;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_info, container, false);



        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scrollView));
        getData();

        initViews(view);


        likeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentRating =Integer.parseInt(mRatingNum.getText().toString().trim());
            if(isChecked) {
                if (dislikeCbx.isChecked()) {
                    dislikeCbx.setChecked(false);
                    mRatingNum.setText(String.valueOf(currentRating+2));
                }
                else
                    mRatingNum.setText(String.valueOf(currentRating+1));
            }
            else
                mRatingNum.setText(String.valueOf(currentRating-1));
            tryChangeVal(LIKE);
        });

        dislikeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int currentRating =Integer.parseInt(mRatingNum.getText().toString().trim());
            if(isChecked) {
                if (likeCbx.isChecked()) {
                    likeCbx.setChecked(false);
                    mRatingNum.setText(String.valueOf(currentRating-2));
                }
                else
                    mRatingNum.setText(String.valueOf(currentRating-1));
            }
            else
                mRatingNum.setText(String.valueOf(currentRating+1));
            tryChangeVal(DISLIKE);
        });

        tryGetStudentsCount();
        tryGetStudentsRating();
        return view;

    }

    private void initViews(View v) {
        likeCbx = (CheckBox) v.findViewById(R.id.like_cbx);
        dislikeCbx = (CheckBox) v.findViewById(R.id.dislike_cbx);
        mRatingNum = (TextView) v.findViewById(R.id.tvRating);
        mSidTextView = (TextView) v.findViewById(R.id.tvSid);
        mDateTextView = (TextView) v.findViewById(R.id.tvDate);
        mTeacherTextView = (TextView) v.findViewById(R.id.tvTeacher);
        mLocTextView = (TextView) v.findViewById(R.id.tvLocation);
        mOnlineNum = (TextView) v.findViewById(R.id.tvOnlineNum);

        mSidTextView.setText(sid);

    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
        }
    }

    private void tryChangeVal(int val){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(sid,val)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseChangeVal,i -> mServerResponse.handleError(i)));
    }

    private void tryGetStudentsCount(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsCount(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCount,i -> mServerResponse.handleError(i)));
    }

    private void tryGetStudentsRating(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsRating(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseRating,i -> mServerResponse.handleError(i)));
    }



    private void handleResponseChangeVal(Response response) {
    }

    private void handleResponseCount(Response response) {
        mOnlineNum.setText(response.getMessage().trim());
    }

    private void handleResponseRating(Response response) {
        mRatingNum.setText(response.getMessage().trim());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        tryGetStudentsCount();
        tryGetStudentsRating();
    }




}