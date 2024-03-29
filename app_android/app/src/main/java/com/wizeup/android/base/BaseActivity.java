package com.wizeup.android.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mindorks.placeholderview.PlaceHolderView;
import com.squareup.picasso.Picasso;
import com.wizeup.android.R;
import com.wizeup.android.TermsActivity;
import com.wizeup.android.course.MyCourseActivity;
import com.wizeup.android.entry.EntryActivity;
import com.wizeup.android.model.NotificationMsg;
import com.wizeup.android.model.User;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.notification.NotificationService;
import com.wizeup.android.notification.NotificationsAdapter;
import com.wizeup.android.profile.ProfileActivity;
import com.wizeup.android.search.SearchActivity;
import com.wizeup.android.session.ConnectSessionActivity;
import com.wizeup.android.settings.ChangeLanguage;
import com.wizeup.android.settings.SettingsActivity;
import com.wizeup.android.utils.Constants;
import com.wizeup.android.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Constants.EMAIL;
import static com.wizeup.android.utils.Constants.NOTIFICATION;
import static com.wizeup.android.utils.Constants.PROFILE_IMG;
import static com.wizeup.android.utils.Constants.USER_NAME;

public class BaseActivity extends AppCompatActivity implements DrawerMenuItem.DrawerCallBack {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView notificationsList;
    private TextView mTvNoResults;
    private ImageView imageNoResults;
    private PullRefreshLayout mSwipeRefreshLayout;
    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private String mEmail;
    private String mId;
    private String mName;
    private String mPic;
    private DrawerHeader mDrawerHeader;
    private NotificationsAdapter mAdapter;
    private ChangeLanguage changeLanguage;
    private String currentNoti;
    private static int ADD_ITEMS = 20;
    private boolean first = true;
    private ShimmerFrameLayout mShimmerViewContainer;
    private View messageDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        changeLanguage = new ChangeLanguage(this);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.search_List));
        mAdapter = new NotificationsAdapter(this, new ArrayList<>());

        initSharedPreferences();
        initViews();
        initSearchView();
        setupDrawer();
        initNoti();
        loadNotifications();

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            mAdapter = new NotificationsAdapter(this, new ArrayList<>());
            first = true;
            loadNotifications();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));



        notificationsList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadNotifications();
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }


    private void initNoti() {
        if (!currentNoti.equalsIgnoreCase("off")) {
            startService(new Intent(getBaseContext(), NotificationService.class));
        }
    }

    private void initViews() {
        messageDialog = findViewById(R.id.msg_public);
        ImageButton userExitMsg = findViewById(R.id.close_msg);
        userExitMsg.setOnClickListener(view -> exitMsg());
        mTvNoResults = findViewById(R.id.tv_no_results);
        imageNoResults = findViewById(R.id.imageBase);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mDrawer = findViewById(R.id.drawerLayout);
        mDrawerView = findViewById(R.id.drawerView);
        mToolbar = findViewById(R.id.toolbar);
        notificationsList = findViewById(R.id.search_List);

    }

    private void exitMsg() {
        messageDialog.setVisibility(View.GONE);
    }

    private void initSearchView() {
        SearchView editSearch = findViewById(R.id.searchView);
        editSearch.setOnClickListener(view -> openSearch());
        ImageView clearButton = editSearch.findViewById(android.support.v7.appcompat.R.id.search_button);
        clearButton.setOnClickListener(view -> openSearch());
    }

    private void openSearch() {
        this.startActivity(new Intent(this, SearchActivity.class));
    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mName = mSharedPreferences.getString(USER_NAME, "");
        mPic = mSharedPreferences.getString(PROFILE_IMG, "");
        mEmail = mSharedPreferences.getString(EMAIL, "");
        mId = mSharedPreferences.getString(Constants.ID, "");
        currentNoti = mSharedPreferences.getString(NOTIFICATION, "");
        String lang = mSharedPreferences.getString(Constants.LANG, "");
        changeLanguage.setLocale(lang);

    }

    private void setupDrawer() {

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
                .addView(new DrawerMenuItem(this, DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT));
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer) {
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
                .subscribe(this::handleResponseProfile, this::handleErrorProfile));
    }

    private void handleResponseProfile(User user) {
        mDrawerHeader.getNameTxt().setText(user.getFname() + " " + user.getLname());
        String pic = user.getProfile_img();
        if (pic != null && !(pic.isEmpty()))
            Picasso.with(this)
                    .load(pic)
                    .error(R.drawable.default_user_image)
                    .into(mDrawerHeader.getProfileImage());
        }

    private void handleErrorProfile(Throwable error) {
        ServerResponse.handleErrorQuiet(error);
        mDrawerHeader.getNameTxt().setText(mName);
        String pic = mPic;
        if (pic != null && !(pic.isEmpty()))
            Picasso.with(this)
                    .load(pic)
                    .error(R.drawable.default_user_image)
                    .into(mDrawerHeader.getProfileImage());
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
        this.startActivity(new Intent(this, ConnectSessionActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onMyCoursesMenuSelected() {
        this.startActivity(new Intent(this, MyCourseActivity.class));
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
        this.startActivity(new Intent(this, SettingsActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onTermsMenuSelected() {
        this.startActivity(new Intent(this, TermsActivity.class));
        mDrawer.closeDrawer(Gravity.START, false);
    }

    @Override
    public void onLogoutMenuSelected() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Log out", (dialog, which) -> logout());
        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });
        builder.show();
    }

    private void logout() {

        stopService(new Intent(getBaseContext(), NotificationService.class));
        RetrofitRequests mRetrofitRequests = new RetrofitRequests(this);
        SharedPreferences.Editor editor = mRetrofitRequests.getSharedPreferences().edit();
        editor.putString(Constants.PASS, "");
        editor.putString(Constants.TOKEN, "");
        editor.putString(Constants.ID, "");
        editor.putString(USER_NAME, "");
        editor.putString(PROFILE_IMG, "");
//        editor.putString(Constants.LANG, "en");
        editor.putString(Constants.NOTIFICATION, "on");
        editor.apply();
        Intent intent = new Intent(this, EntryActivity.class);
        this.startActivity(intent);

        Intent EntryActivity = new Intent(BaseActivity.this, EntryActivity.class);
        EntryActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(EntryActivity);

        }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadNotifications() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getNotifications(mAdapter.getNotificationList().size(), ADD_ITEMS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseNotifications,i -> mServerResponse.handleErrorDown(i)));

    }


    private void handleResponseNotifications(NotificationMsg notificationMsg[]) {
        if (first) {
            if (notificationMsg.length != 0) {
                ArrayList<NotificationMsg> saveNotificationMsg = new ArrayList<>(Arrays.asList(notificationMsg));
                mTvNoResults.setVisibility(View.GONE);
                imageNoResults.setVisibility(View.GONE);
                mAdapter = new NotificationsAdapter(this, new ArrayList<>(saveNotificationMsg));
                notificationsList.setAdapter(mAdapter);
                first = false;
            } else {
                mTvNoResults.setVisibility(View.VISIBLE);
                imageNoResults.setVisibility(View.VISIBLE);
            }
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);

        } else if (notificationMsg.length != 0) {
            ArrayList<NotificationMsg> saveNotificationMsgs = new ArrayList<>(Arrays.asList(notificationMsg));
            mAdapter.getNotificationList().addAll(saveNotificationMsgs);
            mAdapter.notifyDataSetChanged();
        }
    }

}