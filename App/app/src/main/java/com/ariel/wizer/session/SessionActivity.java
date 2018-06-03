package com.ariel.wizer.session;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ImageButton buttonBack;
    private ImageButton buttonVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private int stopPosition;
    private Session session;
    private FullscreenVideoLayout vid;
    private RelativeLayout mVideoViewRelative;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.main_layout));

        if (!getData()) {
            finish();
        }
        initViews();

        buttonVid.setOnClickListener(v -> {
            if (mVideoViewRelative.getVisibility() == View.VISIBLE) {
                stopPosition = vid.getCurrentPosition();
                vid.pause();
                mVideoViewRelative.setVisibility(View.GONE);
            } else {
                mVideoViewRelative.setVisibility(View.VISIBLE);
                vid.seekTo(stopPosition);
            }
            buttonVid.setRotation(buttonVid.getRotation() + 180);
        });

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final SessionPagerAdapter adapter = new SessionPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), session.getSid());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

//        if (session.getVideoID() != null && !(session.getVideoID().isEmpty()))
        playVideo();
    }


    private void initViews() {
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> goBack());
        buttonVid = (ImageButton) findViewById(R.id.vid_button_close);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Session"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        vid = (FullscreenVideoLayout) findViewById(R.id.videoView);
        mVideoViewRelative = findViewById(R.id.videoViewRelative);
    }

    private void playVideo() {

        vid.setActivity(this);

        Uri videoUri = Uri.parse(session.getVideoID());
        try {
            vid.setVideoURI(videoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /////////////Token to vid////////////
//        String url = "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4";//rm
//        Map<String, String> header = new HashMap<String, String>(1);
//        header.put(Constants.TOKEN_HEADER, mRetrofitRequests.getToken());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            vid.setVideoURI(Uri.parse(url), header);
//        } else {
//            Method setVideoURIMethod = null;
//            try {
//                setVideoURIMethod = vid.getClass().getMethod("setVideoURI", Uri.class, Map.class);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (setVideoURIMethod != null) {
//                    setVideoURIMethod.invoke(vid, Uri.parse(url), header);
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
//        mMediaController = new MediaController(this);
//        mMediaController.setAnchorView(vid);
//        vid.setMediaController(mMediaController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            sendSidSms();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        disconnectFromSession();
        finish();
    }

    private void sendSidSms() {
        String smsBody="Hi, please join my session.\nSession ID: "+session.getSid();
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", smsBody);
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            Session _session = (Session) getIntent().getExtras().getParcelable("session");
            if (_session != null) {
                session = _session;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void disconnectFromSession() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().disconnect(session.getSid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Response response) {
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        vid.pause();
    }
}