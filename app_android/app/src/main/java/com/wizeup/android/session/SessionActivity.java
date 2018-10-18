package com.wizeup.android.session;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.wizeup.android.R;
import com.wizeup.android.dialogs.UploadingDialog;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.Session;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.settings.ChangeLanguage;
import com.wizeup.android.utils.Constants;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.network.RetrofitRequests.getBytes;

public class SessionActivity extends AppCompatActivity implements UploadingDialog.OnCallbackCancel {
    private TabLayout tabLayout;
    private ImageButton buttonVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private int stopPosition;
    private Session session;
    private FullscreenVideoLayout vid;
    private RelativeLayout mVideoViewRelative;
    private String nickname;
    private static final int INTENT_REQUEST_CODE = 100;
    public static final String TAG = SessionActivity.class.getSimpleName();

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


        Target viewTarget = new ViewTarget(R.id.vid_button_close, this);
        new ShowcaseView.Builder(this)
                .setTarget(viewTarget)
                .setContentTitle("Press the arrow to open the\n\nvideo when video is available.")
                .singleShot(41)
                .build();


        buttonVid.setOnClickListener(v -> {
            if (session.getVideoUrl() == null || (session.getVideoUrl().isEmpty())) {
                pullSession();
                if (session.getVideoUrl() == null || (session.getVideoUrl().isEmpty())){
                    mServerResponse.downSnackBarMessage("No video.");
                    return;
                }
            }
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

        final ViewPager viewPager = findViewById(R.id.pager);
        final SessionPagerAdapter adapter = new SessionPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                session.getSid(),session.getAdmin(),nickname);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        if (session.getVideoUrl() !=null && !(session.getVideoUrl().isEmpty())) {
            playVideo();
        }
    }


    private void initViews() {
        Button buttonBack =  findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> goBack());
        buttonVid = findViewById(R.id.vid_button_close);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Session"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        vid = findViewById(R.id.videoView);
        mVideoViewRelative = findViewById(R.id.videoViewRelative);
    }

    private void loadLocale() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = mSharedPreferences.getString(Constants.LANG, "");
        ChangeLanguage changeLanguage = new ChangeLanguage(this);
        changeLanguage.setLocale(lang);
    }


    private void playVideo() {
        String url = session.getVideoUrl().trim();


        vid.setActivity(this);
        Uri videoUri = Uri.parse(url);

        try {
            vid.setVideoURI(videoUri);
            vid.showControls();
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

        getMenuInflater().inflate(R.menu.menu_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            sendSidSms();
            return true;
        }
        if (id == R.id.action_vid) {
            uploadVid();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goBack() {
        disconnectFromSession();
        finish();
    }

    private void sendSidSms() {
        String smsBody = "I would like to invite you to join our session in wizeUp.\nSession: " + session.getSid();

        Intent sharingIntent = new Intent ( android.content.Intent.ACTION_SEND );
        sharingIntent.setType ( "text/plain" );
        sharingIntent.putExtra ( android.content.Intent.EXTRA_TEXT, smsBody );
        startActivity(Intent.createChooser(sharingIntent, "Share using?"));

    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            Session _session = getIntent().getExtras().getParcelable("session");
            String _nickname = getIntent().getExtras().getString("nickname");
            if (_session != null && _nickname != null) {
                session = _session;
                nickname = _nickname;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void uploadVid() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/mp4");
        try {
            startActivityForResult(intent, INTENT_REQUEST_CODE);

        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {

                    showLoadingDialog();

                    InputStream is = getContentResolver().openInputStream(data.getData());
                    tryUploadVid(getBytes(is));
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tryUploadVid(byte[] bytes) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("recfile", "video.mp4", requestFile);
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().uploadVid(session.getSid(), body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUploadVid, this::handleError));

    }

    private void handleResponseUploadVid(Response response) {
        hideLoadingDialog();
        mServerResponse.downSnackBarMessage(response.getMessage());
        pullSession();
    }

    public void handleError(Throwable error) {
        hideLoadingDialog();
        mServerResponse.handleErrorDown(error);
    }

    public void showLoadingDialog() {
        UploadingDialog fragment = (UploadingDialog) getSupportFragmentManager().findFragmentByTag(UploadingDialog.TAG);
        if (fragment == null) {
            fragment = new UploadingDialog();
            fragment.setCancelable(false);
            getSupportFragmentManager().beginTransaction()
                    .add(fragment, UploadingDialog.TAG)
                    .commitAllowingStateLoss();
            }
    }

    public void hideLoadingDialog() {
        UploadingDialog fragment = (UploadingDialog) getSupportFragmentManager().findFragmentByTag(UploadingDialog.TAG);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }



    private void pullSession() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getSessionById(session.getSid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePullSession, i -> mServerResponse.handleErrorDown(i)));
    }

    private void handleResponsePullSession(Session _session) {
        session = _session;
        if (session.getVideoUrl() != null && !(session.getVideoUrl().isEmpty())) {
            playVideo();
        }
    }

    private void disconnectFromSession() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().disconnect(session.getSid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleErrorDown(i)));
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
        loadLocale();

    }

    @Override
    public void onPause() {
        super.onPause();
        vid.pause();
    }

    @Override
    public void cancelUpload() {
        mSubscriptions.unsubscribe();
        mSubscriptions = new CompositeSubscription();
    }

}