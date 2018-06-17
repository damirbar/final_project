package com.ariel.wizeup.session;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
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

            createSession(session);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
