package com.wizeup.android.session;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.SessionMessage;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionPostActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;

    private EditText mPostText;
    private Button buttonSend;
    private final String question = "question";
    private String sid;
    private TextView mTextCount;
    private final int MAX_COUNT = 400;
    private String mId;
    private String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_post);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.laout_post));
        initSharedPreferences();
        if (!getData()) {
            finish();
        }
        initViews();
    }

    private void initViews() {
        mTextCount = findViewById(R.id.count_num);
        mPostText = findViewById(R.id.post_text);
        Button buttonBack = findViewById(R.id.cancel_button);
        buttonSend = findViewById(R.id.save_button);
        buttonSend.setOnClickListener(view -> attemptSendPost());
        buttonBack.setOnClickListener(view -> finish());
        mPostText.addTextChangedListener(mTextEditorWatcher);
        mPostText.setText("");
    }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID, "");
    }


    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _sid = getIntent().getExtras().getString("sid");
            String _nickname = getIntent().getExtras().getString("nickname");
            if(_sid != null && _nickname != null) {
                sid = _sid;
                nickname = _nickname;
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
        message.setPoster_id(mId);
        message.setNickname(nickname);
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
