package com.ariel.wizeup.course;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.CourseMessage;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CoursePostActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;

    private EditText mPostText;
    private Button buttonSend;
    private final String question = "question";
    private String cid;
    private TextView mTextCount;
    private final int MAX_COUNT = 400;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_post);
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
            String _sid = getIntent().getExtras().getString("cid");
            if(_sid != null) {
                cid = _sid;
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
        CourseMessage message = new  CourseMessage();
        message.setCid(cid);
        message.setType(question);
        message.setBody(strMessage);
        message.setPoster_id(mId);
        sendPost(message);
    }

    private void sendPost(CourseMessage  message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishCoureMessage(message)
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
