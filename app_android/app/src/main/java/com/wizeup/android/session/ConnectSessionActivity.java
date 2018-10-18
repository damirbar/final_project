package com.wizeup.android.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.wizeup.android.R;
import com.wizeup.android.model.Session;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Validation.validateFields;

public class ConnectSessionActivity extends AppCompatActivity {

    private String sid;
    private EditText mEditTextSid;
    private EditText mEditTextName;
    private Button mCreateSessionButton;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initViews();

        if (getData()) {
            mEditTextSid.setText(sid);
        }


    }

    private void initViews() {
        Button mBtLogin = findViewById(R.id.classloginButton);
        mCreateSessionButton = findViewById(R.id.create_session_button);
        mEditTextSid = findViewById(R.id.edit_text_sid);
        mEditTextName = findViewById(R.id.edit_text_name);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        mBtLogin.setOnClickListener(view -> login());
        mCreateSessionButton.setOnClickListener(view -> createSession());
        buttonBack.setOnClickListener(view -> finish());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _sid = getIntent().getExtras().getString("sid");
            if (_sid != null) {
                sid = _sid;
                return true;
            } else
                return false;
        } else
            return false;
    }


    private void createSession() {
        this.startActivity(new Intent(this, CreateSessionActivity.class));

    }


    private void login() {

        sid = mEditTextSid.getText().toString().trim();
        nickname = mEditTextName.getText().toString().trim();


        if (!validateFields(sid)) {
            mServerResponse.showSnackBarMessage("Session Id should not be empty.");
            return;

        }

        if (!validateFields(nickname)) {
            nickname = "Anon";
        }

        loginProcess(sid, nickname);
    }

    private void loginProcess(String sid, String name) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().connectSession(sid, name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Session session) {
        Intent intent = new Intent(getBaseContext(), SessionActivity.class);
        intent.putExtra("session", session);
        intent.putExtra("nickname", nickname);

        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
