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
import com.ariel.wizer.model.Session;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Validation.validateFields;


public class CreateSessionActivity extends AppCompatActivity {

    private String sid;
    private EditText mEditTextSid;
    private EditText mEditTextName;
    private EditText mEditTextLoc;
    private Button mBtLogin;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private ImageButton buttonBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initViews();

    }

    private void initViews() {
        mBtLogin = (Button) findViewById(R.id.create_session_button);
        mEditTextSid = (EditText) findViewById(R.id.edit_text_sid);
        mEditTextName = (EditText) findViewById(R.id.edit_text_name);
        mEditTextLoc = (EditText) findViewById(R.id.edit_text_loc);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
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
        Intent intent = new Intent(getBaseContext(),SessionTabActivity.class);
        intent.putExtra("sid",sid);
        startActivity(intent);
        finish();
    }

    private void setError() {
        mEditTextSid.setError(null);
        mEditTextLoc.setError(null);
        mEditTextName.setError(null);
    }


    private void login() {

        setError();

        String loc = mEditTextLoc.getText().toString().trim();
        sid = mEditTextSid.getText().toString().trim();
        String name = mEditTextName.getText().toString().trim();

        int err = 0;

        if (!validateFields(sid)) {
            err++;
            mEditTextSid.setError("Session Id should be valid !");
        }

        if (!validateFields(name)) {
            err++;
            mEditTextName.setError("Session Name should be valid !");
        }

        if (!validateFields(name)) {
            err++;
            mEditTextLoc.setError("Session Location should be valid !");
        }

        if (err == 0) {

            Session session = new Session();
            session.setSid(sid);
            session.setLocation(loc);
            session.setName(name);

            createSession(session);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
