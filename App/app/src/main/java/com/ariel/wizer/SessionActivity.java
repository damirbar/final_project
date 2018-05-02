package com.ariel.wizer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.chat.ChatMessageAdapter;
import com.ariel.wizer.model.ChatMessage;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionActivity extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private Button submitButton;
    private RatingBar simpleRatingBar;
    private TextView mTvClassAvg;
    private TextView mTvClassPin;
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private String pin = "";
    private final int delay = 5000; //milliseconds

    private SessionMessagesAdapter mAdapter;
//    private SessionMessage sessionMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_session));
        getPin();
        initViews();
        pullMessages();
//        classAvgProcess();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                classAvgProcess();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
    }

    private void initViews() {
        mTvClassPin = (TextView) findViewById(R.id.tVclassPin);
        mTvClassPin.setText("Class Pin:" + pin);
        mTvClassAvg = (TextView) findViewById(R.id.tVclassAvg);
        simpleRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        submitButton = (Button) findViewById(R.id.submitButton);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        messagesList = (ListView) findViewById(R.id.sessionMessagesList);

//        submitButton.setOnClickListener(view -> rateClass());
    }

    private void getPin() {
        Intent intent = getIntent();
        pin = intent.getExtras().getString("pin");
    }

//    private void classAvgProcess() {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsCount(pin)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseAvg,i -> mServerResponse.handleError(i)));
//    }
//
//    private void handleResponseAvg(Response response) {
//        mTvClassAvg.setText("Rating:" + response.getMessage());
//    }


//    private void rateClass() {
//        String rate =  Integer.toString((int)simpleRatingBar.getRating());
//        String rating = "Rating :: " + simpleRatingBar.getRating();
//        Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
//        rateProcess(rate);
//
//    }
//
//    private void rateProcess(String rate) {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(pin,rate)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseRateProcess,i -> mServerResponse.handleError(i)));
//    }
//
//    private void handleResponseRateProcess(Response response) {
//        mServerResponse.showSnackBarMessage(response.getMessage());
//    }
//








    private void pullMessages(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessages(pin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(SessionMessage[] messages) {
        if(messages.length == 0){
            mTvNoResults.setVisibility(View.VISIBLE);
        }
        else{
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SessionMessagesAdapter(this, new ArrayList<>(Arrays.asList(messages)));
            messagesList.setAdapter(mAdapter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
