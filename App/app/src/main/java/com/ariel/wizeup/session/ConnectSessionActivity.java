package com.ariel.wizeup.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Validation.validateFields;

public class ConnectSessionActivity extends AppCompatActivity {

    private String sid;
    private EditText mEditTextSid;
    private EditText mEditTextName;
    private Button mCreateSessionButton;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initViews();

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


    private void createSession() {
        this.startActivity(new Intent (this, CreateSessionActivity.class));

    }


    private void login() {

        sid = mEditTextSid.getText().toString().trim();
        String name = mEditTextName.getText().toString().trim();


        if (!validateFields(sid)) {
            mServerResponse.showSnackBarMessage("Session Id should not be empty.");
            return;

        }

        if (!validateFields(name)) {
            name = "Anon";
        }

            loginProcess(sid,name);
    }

    private void loginProcess(String sid, String name) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().connectSession(sid,name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Session session){
        Intent intent = new Intent(getBaseContext(), SessionActivity.class);
        intent.putExtra("session",session);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
