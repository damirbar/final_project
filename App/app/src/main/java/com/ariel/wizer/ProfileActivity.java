package com.ariel.wizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.squareup.picasso.Picasso;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton buttonBack;
    private TextView mDisplayName;
    private TextView mCountry;
    private TextView mAboutMe;
    private TextView btunEditProfile;
    private ImageView image;

    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String mId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout_profile));
        initSharedPreferences();
        initViews();
        loadProfile();
    }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getmSharedPreferences().getString(Constants.ID,"");
    }


    private void initViews() {
        image = (ImageView)findViewById(R.id.user_profile_photo);
        mDisplayName = (TextView) findViewById(R.id.tvdisName);
        mCountry = (TextView) findViewById(R.id.tvcountry_);
        mAboutMe = (TextView) findViewById(R.id.tvAboutMe);
        btunEditProfile = (TextView) findViewById(R.id.edit_profile);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view ->finish());
        btunEditProfile.setOnClickListener(view ->  editProfile());

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
        if(!(disName.isEmpty())){
            mDisplayName.setText(user.getDisplay_name());
        }
        else{
            mDisplayName.setText(user.getFname()+" " + user.getLname());
        }
        mCountry.setText(user.getCountry());
        mAboutMe.setText(user.getAbout_me());
        Picasso.get().load("https://scontent.fsdv2-1.fna.fbcdn.net/v/t1.0-9/22730135_839880432861616_8306732533151605396_n.jpg?_nc_cat=0&oh=1597bce378ebaa853df982319d669bf5&oe=5B9303BC").into(image);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadProfile();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
