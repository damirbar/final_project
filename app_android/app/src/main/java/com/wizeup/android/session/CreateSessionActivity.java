package com.wizeup.android.session;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.wizeup.android.R;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.Session;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Validation.validateFields;


public class CreateSessionActivity extends AppCompatActivity {

    private String sid;
    private EditText mEditTextSid;
    private EditText mEditTextName;
    private EditText mEditTextLoc;

    private Button mBtLogin;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private Button buttonBack;
    private Session session;
    private String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initSharedPreferences();
        initViews();

    }

    private void initViews() {
        mBtLogin = findViewById(R.id.save_button);
        mEditTextSid = findViewById(R.id.edit_text_sid);
        mEditTextName = findViewById(R.id.edit_text_name);
        mEditTextLoc = findViewById(R.id.edit_text_loc);
        buttonBack = findViewById(R.id.cancel_button);
        mBtLogin.setOnClickListener(view -> login());
        buttonBack.setOnClickListener(view -> finish());

    }

    private void initSharedPreferences() {
        email = mRetrofitRequests.getSharedPreferences().getString(Constants.EMAIL, "");
    }


    private void createSession(Session session) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().createSession(session)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCreateSession,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseCreateSession(Response response) {
        Intent intent = new Intent(getBaseContext(),SessionActivity.class);
        intent.putExtra("session",session);
        intent.putExtra("nickname", "");

        startActivity(intent);
        finish();
    }


    private void login() {


        String loc = mEditTextLoc.getText().toString().trim();
        sid = mEditTextSid.getText().toString().trim();
        String name = mEditTextName.getText().toString().trim();

        if (!validateFields(sid)) {
            mServerResponse.showSnackBarMessage("Session Id should not be empty.");
            return;
        }

        if (!validateFields(name)) {
            mServerResponse.showSnackBarMessage("Session Name should not be empty.");
            return;

        }

        if (!validateFields(name)) {
            mServerResponse.showSnackBarMessage("Session Location should not be empty.");
            return;

        }

            session = new Session();
            session.setSid(sid);
            session.setLocation(loc);
            session.setName(name);
            session.setAdmin(email);

            createSession(session);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
