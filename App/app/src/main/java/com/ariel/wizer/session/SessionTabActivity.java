package com.ariel.wizer.session;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ariel.wizer.R;
import com.ariel.wizer.fragments.LoginFragment;
import com.ariel.wizer.network.RetrofitRequests;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionTabActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ImageButton buttonBack;
    private ImageButton buttonCloseVid;
    private FrameLayout frameVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private VideoPlayerFragment mVideoPlayerFragment;
    private int stopPosition;
    private String sid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_tab);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        if (!getData()) {
            finish();
        }
        initViews();

        buttonCloseVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frameVid.getVisibility() == View.VISIBLE) {
                    frameVid.setVisibility(View.GONE);
                    stopPosition = mVideoPlayerFragment.getVid().getCurrentPosition();
                    mVideoPlayerFragment.getVid().pause();
                    mVideoPlayerFragment.getVid().setMediaController(null);
                    }
                else{
                    frameVid.setVisibility(View.VISIBLE);
                    mVideoPlayerFragment.getVid().seekTo(stopPosition);
                    mVideoPlayerFragment.getVid().start();
                    mVideoPlayerFragment.getVid().setMediaController(mVideoPlayerFragment.getmMediaController());

                }
                buttonCloseVid.setRotation(buttonCloseVid.getRotation() + 180);

            }
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

        if (savedInstanceState == null) {

            loadFragment();
        }

    }

    private void initViews() {
        frameVid = (FrameLayout) findViewById(R.id.frame_vid);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> goBack());
        buttonCloseVid= (ImageButton) findViewById(R.id.vid_button_close);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Session"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void loadFragment(){

        if (mVideoPlayerFragment == null) {
            Bundle bundle = new Bundle();
            mVideoPlayerFragment = new VideoPlayerFragment();
            bundle.putString("sid", sid);
            mVideoPlayerFragment.setArguments(bundle);
        }
        getFragmentManager().beginTransaction().replace(R.id.frame_vid,mVideoPlayerFragment,LoginFragment.TAG).commit();
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

}