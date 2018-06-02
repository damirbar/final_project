package com.ariel.wizer.session;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static com.ariel.wizer.network.RetrofitRequests.getBytes;

public class SessionInfoFragment extends Fragment {

    private CheckBox likeCbx;
    private CheckBox dislikeCbx;
    private TextView mRatingNum;
    private TextView mSidTextView;
    private Button btnVid;

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
    private static final int INTENT_REQUEST_CODE = 100;


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
        btnVid = (Button) v.findViewById(R.id.button_vid);
        btnVid.setOnClickListener(view -> uploadVid());
        mSidTextView.setText(sid);

    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
        }
    }

    private void uploadVid() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/mp4");
        try {
            startActivityForResult(intent, INTENT_REQUEST_CODE);

        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                    tryUploadVid(getBytes(is));
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tryUploadVid(byte[] bytes) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("recfile", "video.mp4", requestFile);
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().uploadVid(body, sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUploadVid, i -> mServerResponse.handleError(i)));
    }


    private void handleResponseUploadVid(Response response) {
        mServerResponse.showSnackBarMessage(response.getMessage());
    }


    private void tryChangeVal(int val) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(sid, val)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseChangeVal, i -> mServerResponse.handleError(i)));
    }

    private void tryGetStudentsCount() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsCount(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCount, i -> mServerResponse.handleError(i)));
    }

    private void tryGetStudentsRating() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsRating(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseRating, i -> mServerResponse.handleError(i)));
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
    public void onResume() {
        super.onResume();
//        tryGetStudentsCount();
//        tryGetStudentsRating();
    }


}