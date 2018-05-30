package com.ariel.wizer.session;

import android.content.res.Configuration;
import android.media.MediaPlayer;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ariel.wizer.R;
import com.ariel.wizer.network.RetrofitRequests;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ImageButton buttonBack;
    private ImageButton buttonCloseVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private int stopPosition;
    private String sid;
    private ProgressBar spinnerView;

    private FullscreenVideoLayout vid;
//    private MediaController mMediaController;
    private RelativeLayout mvideoViewRelative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        if (!getData()) {
            finish();
        }
        initViews();

        buttonCloseVid.setOnClickListener(v -> {
            if (mvideoViewRelative.getVisibility() == View.VISIBLE) {
                stopPosition = vid.getCurrentPosition();
                vid.pause();
//                vid.setMediaController(null);
                mvideoViewRelative.setVisibility(View.GONE);
            }
            else{
                mvideoViewRelative.setVisibility(View.VISIBLE);
                vid.seekTo(stopPosition);
                vid.start();
//                vid.setMediaController(mMediaController);
            }
            buttonCloseVid.setRotation(buttonCloseVid.getRotation() + 180);

        });


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final SessionPagerAdapter adapter = new SessionPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),sid);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        playVideo();

    }


    private void initViews() {
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> goBack());
        buttonCloseVid= (ImageButton) findViewById(R.id.vid_button_close);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Session"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        vid = (FullscreenVideoLayout) findViewById(R.id.videoView);
        mvideoViewRelative = findViewById(R.id.videoViewRelative);
        spinnerView = (ProgressBar) findViewById(R.id.my_spinner);
        vid.setOnInfoListener(onInfoToPlayStateListener);

    }

    private final MediaPlayer.OnInfoListener onInfoToPlayStateListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                spinnerView.setVisibility(View.GONE);
            }
            if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                spinnerView.setVisibility(View.VISIBLE);
            }
            if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                spinnerView.setVisibility(View.VISIBLE);
            }
            return false;
        }
    };

    private void playVideo() {

        vid.setActivity(this);

        Uri videoUri = Uri.parse("http://res.cloudinary.com/wizeup/video/upload/v1527153142/1234video.mp4");
        try {
            vid.setVideoURI(videoUri);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        String url = "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4";//rm
//
//        Map<String, String> header = new HashMap<String, String>(1);
//        header.put(Constants.TOKEN_HEADER, mRetrofitRequests.getmToken());
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
        if (id == R.id.action_settings1) {
            return true;
        }
        if (id == R.id.action_settings2) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        disconnectFromSession();
        finish();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}