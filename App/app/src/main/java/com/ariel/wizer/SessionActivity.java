package com.ariel.wizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionActivity extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private Button submitButton;
    private RatingBar simpleRatingBar;
    private TextView mTvClassAvg;
    private TextView mTvClassPin;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String pin = "";
    private final int delay = 5000; //milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_session));
        getPin();
        initViews();
        classAvgProcess();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                classAvgProcess();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void initViews() {
        mTvClassPin = (TextView) findViewById(R.id.tVclassPin);
        mTvClassPin.setText("Class Pin:" + pin);
        mTvClassAvg = (TextView) findViewById(R.id.tVclassAvg);
        simpleRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(view -> rateClass());
    }

    private void getPin() {
        Intent intent = getIntent();
        pin = intent.getExtras().getString("pin");
    }

    private void classAvgProcess() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsCount(pin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAvg,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseAvg(Response response) {
        mTvClassAvg.setText("Rating:" + response.getMessage());
    }


    private void rateClass() {
        String rate =  Integer.toString((int)simpleRatingBar.getRating());
        String rating = "Rating :: " + simpleRatingBar.getRating();
        Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
        rateProcess(rate);

    }

    private void rateProcess(String rate) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(pin,rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Response response) {
        mServerResponse.showSnackBarMessage(response.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
