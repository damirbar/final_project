package com.ariel.wizeup.session;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
    private Button buttonBack;
    private Button buttonSend;
    private final String question = "question";
    private String sid;
    private TextView mTextCount;
    private final int MAX_COUNT = 400;

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
        mTextCount = findViewById(R.id.count_num);
        mPostText = findViewById(R.id.post_text);
        buttonBack = findViewById(R.id.cancel_button);
        buttonSend = findViewById(R.id.save_button);
        buttonSend.setOnClickListener(view -> attemptSendPost());
        buttonBack.setOnClickListener(view -> finish());
        mPostText.addTextChangedListener(mTextEditorWatcher);

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
        message.setBody(strMessage);
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


    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int num = MAX_COUNT - s.length();
            mTextCount.setText(String.valueOf(num));
            if (num < 0) {
                buttonSend.setEnabled(false);
                buttonSend.setTextColor(Color.parseColor("#e7dada"));
                mTextCount.setTextColor(Color.RED);
            }
            else{
                buttonSend.setEnabled(true);
                mTextCount.setTextColor(Color.parseColor("#808080"));
                buttonSend.setTextColor(Color.WHITE);
            }
        }
        public void afterTextChanged(Editable s) {
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
