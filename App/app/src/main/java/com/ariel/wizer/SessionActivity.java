package com.ariel.wizer;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Constants.MAIL;
import static com.ariel.wizer.utils.Validation.validateFields;

public class SessionActivity extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private Button submitButton;
    private RatingBar simpleRatingBar;
    private TextView mTvClassAvg;
    private TextView mTvClassPin;


    private String pin = "";
    private int delay = 5000; //milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        mSubscriptions = new CompositeSubscription();
        Intent intent = getIntent();
        pin = intent.getExtras().getString("pin");
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

    private void classAvgProcess() {
        mSubscriptions.add(NetworkUtil.getRetrofit().getStudentsCount(pin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseAvg,this::handleError));
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

    private void showSnackBarMessage(String message) {
//        Snackbar.make(findViewById(R.id.activity_main),message, Snackbar.LENGTH_SHORT).show();///////////?

    }

    private void rateProcess(String rate) {
        mSubscriptions.add(NetworkUtil.getRetrofit().changeVal(pin,rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        showSnackBarMessage(response.getMessage());
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
