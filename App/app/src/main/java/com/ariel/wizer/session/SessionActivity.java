package com.ariel.wizer.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionActivity extends AppCompatActivity  {

    private ImageButton buttonBack;
    private ImageButton buttonVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private String sid;


    public static final String TAG = SessionActivity.class.getSimpleName();
    private SessionFeedFragment mSessionFeedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        if (!getData()) {
            finish();
        }
        sendData();
        initViews();
        if (savedInstanceState == null) {
            loadFragment();
        }
    }

    private void initViews() {
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonVid = (ImageButton) findViewById(R.id.image_Button_cam);
        buttonVid.setOnClickListener(view -> goVid());

        buttonBack.setOnClickListener(view -> goBack());

    }

    private void goBack() {
        disconnectFromSession();
        finish();
    }

    private void goVid() {
        VideoPlayerActivity.showRemoteVideo(this,sid);
    }


    private void loadFragment(){
        if (mSessionFeedFragment == null) {

            mSessionFeedFragment = new SessionFeedFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.frame_session, mSessionFeedFragment, SessionFeedFragment.TAG).commit();
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _sid = getIntent().getExtras().getString("sid");
            if(_sid != null) {
                sid = _sid;
                return true;
            } else
                return false;
        }
        else
            return false;
    }

    private void sendData() {
        Bundle bundle = new Bundle();
        bundle.putString("sid",sid);
        mSessionFeedFragment = new SessionFeedFragment();
        mSessionFeedFragment.setArguments(bundle);
    }

    private void disconnectFromSession(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().disconnect(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }

    @Override
    public void onDestroy() {
        disconnectFromSession();
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        disconnectFromSession();
        super.onBackPressed();
        finish();
    }

}
