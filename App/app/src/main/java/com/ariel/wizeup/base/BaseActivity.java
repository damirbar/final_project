package com.ariel.wizeup.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.SettingInjectorService;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.TermsActivity;
import com.ariel.wizeup.course.MyCourseActivity;
import com.ariel.wizeup.entry.EntryActivity;
import com.ariel.wizeup.model.User;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.profile.ProfileActivity;
import com.ariel.wizeup.search.SearchActivity;
import com.ariel.wizeup.session.ConnectSessionActivity;
import com.ariel.wizeup.settings.SettingsActivity;
import com.ariel.wizeup.utils.Constants;
import com.mindorks.placeholderview.PlaceHolderView;
import com.squareup.picasso.Picasso;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Constants.EMAIL;

public class BaseActivity extends AppCompatActivity implements DrawerMenuItem.DrawerCallBack {

    private RetrofitRequests mRetrofitRequests;
//    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView searchList;
    private TextView mTvNoResults;
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private String mEmail;
    private String mId;
    private SearchView editSearch;
    private DrawerHeader mDrawerHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
//        mServerResponse = new ServerResponse(findViewById(R.id.search_List));
        initSharedPreferences();
        initViews();
        setupDrawer();
    }

    private void initViews() {
        mDrawer = (DrawerLayout)findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView)findViewById(R.id.drawerView);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        searchList = (ListView) findViewById(R.id.search_List);
        initSearchView();

    }

    private void initSearchView() {
        editSearch = (SearchView) findViewById(R.id.searchView);
        editSearch.setOnClickListener(view -> openSearch());
        ImageView clearButton = (ImageView) editSearch.findViewById(android.support.v7.appcompat.R.id.search_button);
        clearButton.setOnClickListener(view -> openSearch());
    }

    private void openSearch() {
        this.startActivity(new Intent(this, SearchActivity.class));
    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEmail = mSharedPreferences.getString(EMAIL,"");
        mId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID,"");
    }

    private void setupDrawer(){

        mDrawerHeader = new DrawerHeader(mEmail);
        mDrawerView
                .addView(mDrawerHeader)
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_PROFILE))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_SESSION))
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_MESSAGE))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_MY_COURSES))
//                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_NOTIFICATIONS))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_TERMS))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS))
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT))
                .addView(new DrawerEnd());

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }


        };
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    private void loadProfile() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getProfile(mId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseProfile, ServerResponse::handleErrorQuiet));
    }

    private void handleResponseProfile(User user) {
        mDrawerHeader.getNameTxt().setText(user.getFname() + " " + user.getLname());
        String pic = user.getProfile_img();
        if(pic!=null&&!(pic.isEmpty()))
            Picasso.get().load(pic).into(mDrawerHeader.getProfileImage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


    @Override
    public void onProfileMenuSelected() {
        this.startActivity(new Intent(this, ProfileActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onSessionMenuSelected() {
        this.startActivity(new Intent (this, ConnectSessionActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onMyCoursesMenuSelected() {
        this.startActivity(new Intent (this, MyCourseActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onMessagesMenuSelected() {
    }

    @Override
    public void onNotificationsMenuSelected() {
    }

    @Override
    public void onSettingsMenuSelected() {
        this.startActivity(new Intent (this, SettingsActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onTermsMenuSelected() {
        this.startActivity(new Intent (this, TermsActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onLogoutMenuSelected() {
        logout();
    }

    private void logout() {
        RetrofitRequests mRetrofitRequests = new RetrofitRequests(this);
        SharedPreferences.Editor editor = mRetrofitRequests.getSharedPreferences().edit();
        editor.putString(Constants.PASS,"");
        editor.putString(Constants.TOKEN,"");
        editor.putString(Constants.ID,"");
        editor.putString(Constants.USER_NAME,"");
        editor.putString(Constants.PROFILE_IMG,"");

        editor.apply();
        Intent intent = new Intent(this, EntryActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadProfile();
    }


}