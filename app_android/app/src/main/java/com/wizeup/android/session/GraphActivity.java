package com.wizeup.android.session;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.settings.ChangeLanguage;
import com.wizeup.android.utils.Constants;
import com.robinhood.spark.SparkView;

import rx.subscriptions.CompositeSubscription;


public class GraphActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private String sid;

    private SparkView sparkView;
    private RandomizedAdapter adapter;
    private TextView scrubInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        getData();
        setContentView(R.layout.activity_graph);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_graph));
        initViews();
    }

    private void initViews() {
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> finish());

        sparkView = findViewById(R.id.sparkview);
        scrubInfoTextView = findViewById(R.id.scrub_info_textview);


        adapter = new RandomizedAdapter();
        sparkView.setAdapter(adapter);
        sparkView.setScrubListener(new SparkView.OnScrubListener() {
            @Override
            public void onScrubbed(Object value) {
                if (value == null) {
                    scrubInfoTextView.setText(R.string.scrub_empty);
                } else {
                    scrubInfoTextView.setText(getString(R.string.scrub_format, value));
                }
            }
        });

    }

    private void loadLocale() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = mSharedPreferences.getString(Constants.LANG, "");
        ChangeLanguage changeLanguage = new ChangeLanguage(this);
        changeLanguage.setLocale(lang);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLocale();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
        loadLocale();
    }


}
