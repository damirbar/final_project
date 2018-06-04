package com.ariel.wizeup.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PostActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;

    private EditText mPostText;
    private ImageButton buttonBack;
    private ImageButton buttonSend;
    private final String question = "question";
    private String sid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.laout_post));
        if (!getData()) {
            finish();
        }
        initViews();
    }

    private void initViews() {
        mPostText = (EditText) findViewById(R.id.post_text);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonSend = (ImageButton) findViewById(R.id.imageButton);
        buttonSend.setOnClickListener(view -> attemptSendPost());
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

    private void attemptSendPost() {
        String strMessage = mPostText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        SessionMessage message = new  SessionMessage();
        message.setSid(sid);
        message.setType(question);
        String Body[]={"",strMessage};
        message.setBody(Body);
        sendPost(message);
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
