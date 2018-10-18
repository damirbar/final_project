package com.wizeup.android.session;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.wizeup.android.R;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.SessionMessage;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class QuizActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;

    private Button buttonSend;
    private final String question = "quiz";
    private String sid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.laout_post));
//        if (!getData()) {
//            finish();
//        }
        initViews();

    }

    private void initViews() {
        Button buttonBack = findViewById(R.id.cancel_button);
        buttonSend = findViewById(R.id.save_button);
        buttonSend.setOnClickListener(view -> attemptSendQuiz());
        buttonBack.setOnClickListener(view -> finish());
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

    private void attemptSendQuiz() {
    }

    private void sendPost(SessionMessage  message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishSessionMessage(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendPost,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseSendPost(Response response) {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
