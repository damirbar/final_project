package com.wizeup.android.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.wizeup.android.R;
import com.wizeup.android.model.User;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.Constants;
import com.squareup.picasso.Picasso;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfileActivity extends AppCompatActivity {

    private TextView mDisplayName;
    private TextView mCountry;
    private TextView mAboutMe;
    private ImageButton mBtEditProfile;
    private ImageView image;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private PullRefreshLayout mSwipeRefreshLayout;
    private String mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout_profile));
        initViews();

        if (!getData()) {
            initSharedPreferences();
        }
        else{
            mBtEditProfile.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setColor(0);
        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            loadProfile();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 0));

    }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID,"");
    }

    private void initViews() {
        image = findViewById(R.id.user_profile_photo);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mDisplayName = findViewById(R.id.tvdName);
        mCountry = findViewById(R.id.tvCountry);
        mAboutMe = findViewById(R.id.tvAboutMe);
        mBtEditProfile = findViewById(R.id.image_edit);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view ->finish());
        mBtEditProfile.setOnClickListener(view ->  editProfile());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _id = getIntent().getExtras().getString("id");
            if(_id != null && !(_id.isEmpty())){
                mId = _id;
                return true;
            } else
                return false;
        }
        else
            return false;
    }


    private void editProfile() {
        Intent myIntent = new Intent(this, EditProfileActivity.class);
        startActivity(myIntent);
    }

    private void loadProfile() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getProfile(mId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseProfile,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseProfile(User user) {
        String disName = user.getDisplay_name();
        if(disName!=null && !(disName.isEmpty())){
            mDisplayName.setText(user.getDisplay_name());
        }
        else{
            mDisplayName.setText(user.getFname()+" " + user.getLname());
        }
        mCountry.setText(user.getCountry());
        mAboutMe.setText(user.getAbout_me());
        String pic = user.getProfile_img();
        if (pic != null && !(pic.isEmpty()))
            Picasso.with(this)
                    .load(pic)
                    .error(R.drawable.default_user_image)
                    .into(image);
    }


    @Override
    protected void onResume(){
        super.onResume();
        loadProfile();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
