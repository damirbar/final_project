package com.wizeup.android.session;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.github.nkzawa.emitter.Emitter;
import com.wizeup.android.R;
import com.wizeup.android.dialogs.UploadingDialog;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.Session;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.settings.ChangeLanguage;
import com.wizeup.android.utils.Constants;

import org.json.JSONObject;

import java.io.InputStream;

import hb.xvideoplayer.MxMediaManager;
import hb.xvideoplayer.MxVideoPlayer;
import hb.xvideoplayer.MxVideoPlayerWidget;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;

import static com.wizeup.android.network.RetrofitRequests.getBytes;
import static smartdevelop.ir.eram.showcaseviewlib.GuideView.DismissType.targetView;

public class SessionActivity extends AppCompatActivity implements UploadingDialog.OnCallbackCancel {
    private TabLayout tabLayout;
    private ImageButton buttonVid;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private Session session;
    private MxVideoPlayerWidget vid;
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
        showCase();

        buttonVid.setOnClickListener(v -> {
            if (session.getVideoUrl() == null || (session.getVideoUrl().isEmpty())) {
                pullSession();
                mServerResponse.downSnackBarMessage("No video.");
                return;
            }
            if (mVideoViewRelative.getVisibility() == View.VISIBLE) {

                if (MxMediaManager.getInstance().getPlayer().isPlaying()) {
                    MxMediaManager.getInstance().getPlayer().pause();
                    vid.setUiPlayState(5);
                    vid.setUiPlayState(5);
                }
                mVideoViewRelative.setVisibility(View.GONE);

            } else {

                mVideoViewRelative.setVisibility(View.VISIBLE);


            }
            buttonVid.setRotation(buttonVid.getRotation() + 180);

        });

        final ViewPager viewPager = findViewById(R.id.pager);
        final SessionPagerAdapter adapter = new SessionPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),
                session.getSid(), session.getAdmin(), nickname);
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

        if (session.getVideoUrl() != null && !(session.getVideoUrl().isEmpty())) {
            playVideo();
        }
    }

    private void showCase() {
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            new GuideView.Builder(this)
                    .setTitle("Video Button")
                    .setContentText("Press the arrow to OPEN the video\n\nand again to CLOSE the video.")
                    .setDismissType(targetView)
                    .setTargetView(buttonVid)
                    .setContentTextSize(14)
                    .setTitleTextSize(17)
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .build()
                    .show();

            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }

    }

    private void initViews() {
        vid = findViewById(R.id.videoView);
        Button buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> exitAlert());
        buttonVid = findViewById(R.id.vid_button_close);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Session"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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

        vid.startPlay(url, MxVideoPlayer.SCREEN_LAYOUT_NORMAL, session.getName());

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

    private void sendSidSms() {
        String smsBody = "I would like to invite you to join our session in wizeUp.\nSession: " + session.getSid();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, smsBody);
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

        if (MxVideoPlayer.backPress()) {
            return;
        }
        exitAlert();
    }

    public void exitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit this session?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disconnectFromSession();
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLocale();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (MxMediaManager.getInstance().getPlayer().isPlaying()) {
            MxMediaManager.getInstance().getPlayer().pause();
            vid.setUiPlayState(5);
            vid.setUiPlayState(5);
        }
    }

    @Override
    public void cancelUpload() {
        mSubscriptions.unsubscribe();
        mSubscriptions = new CompositeSubscription();
    }

}