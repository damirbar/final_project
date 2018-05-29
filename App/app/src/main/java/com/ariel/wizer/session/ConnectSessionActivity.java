package com.ariel.wizer.session;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Validation.validateFields;

public class ConnectSessionActivity extends AppCompatActivity {

    private String sid;
    private EditText mEditTextSid;
    private EditText mEditTextName;

    private Button mBtLogin;
    private Button mCreateSessionButton;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private ImageButton buttonBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_session);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initViews();
    }

    private void initViews() {
        mBtLogin = (Button) findViewById(R.id.classloginButton);
        mCreateSessionButton = (Button) findViewById(R.id.create_session_button);
        mEditTextSid = (EditText) findViewById(R.id.edit_text_sid);
        mEditTextName = (EditText) findViewById(R.id.edit_text_name);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        mBtLogin.setOnClickListener(view -> login());
        mCreateSessionButton.setOnClickListener(view -> createSession());
        buttonBack.setOnClickListener(view -> finish());


    }


    private void createSession() {
        this.startActivity(new Intent (this, CreateSessionActivity.class));

    }

    private void setError() {
        mEditTextName.setError(null);
        mEditTextSid.setError(null);
    }


    private void login() {

        setError();

        sid = mEditTextSid.getText().toString().trim();
        String name = mEditTextName.getText().toString().trim();

        int err = 0;

        if (!validateFields(sid)) {
            err++;
            mEditTextSid.setError("Session should be valid !");
        }

        if (!validateFields(name)) {
            err++;
            mEditTextName.setError("User Name should be valid !");
        }


        if (err == 0) {
            loginProcess(sid,name);
        }
    }

    private void loginProcess(String sid, String name) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().connectSession(sid,name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Response response) {
        Intent intent = new Intent(getBaseContext(), SessionActivity.class);
        intent.putExtra("sid",sid);
        startActivity(intent);
        mEditTextSid.setText("");
        finish();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
