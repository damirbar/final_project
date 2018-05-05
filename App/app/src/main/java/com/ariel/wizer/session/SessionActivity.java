package com.ariel.wizer.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ariel.wizer.R;

public class SessionActivity extends AppCompatActivity  {

    public static final String TAG = SessionActivity.class.getSimpleName();
    private String sid;

    private SessionFeedFragment mSessionFeedFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        getSid();
        initViews();
        sendData();
        if (savedInstanceState == null) {
            loadFragment();
        }
    }

    private void initViews() {
    }


    private void loadFragment(){
        if (mSessionFeedFragment == null) {

            mSessionFeedFragment = new SessionFeedFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.frame_session, mSessionFeedFragment, SessionFeedFragment.TAG).commit();
    }

    private void getSid() {
        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
    }

    private void sendData() {
        Bundle bundle = new Bundle();
        bundle.putString("sid",sid);
        mSessionFeedFragment = new SessionFeedFragment();
        mSessionFeedFragment.setArguments(bundle);

        Intent intent = new Intent(getBaseContext(), PostActivity.class);
        intent.putExtra("sid",sid);

    }


}
