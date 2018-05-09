package com.ariel.wizer.session;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.ariel.wizer.R;
import com.ariel.wizer.network.RetrofitRequests;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionTabActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ImageButton buttonBack;
    private ImageButton buttonVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private SessionFeedFragment mSessionFeedFragment;
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
        sendData();

        initViews();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),sid);
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
    }

    private void initViews() {
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonVid = (ImageButton) findViewById(R.id.image_Button_cam);
        buttonVid.setOnClickListener(view -> goVid());
        buttonBack.setOnClickListener(view -> goBack());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.addTab(tabLayout.newTab().setText("Friends"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        disconnectFromSession();
        finish();
    }

    private void goVid() {
        VideoPlayerActivity.showRemoteVideo(this,sid);
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