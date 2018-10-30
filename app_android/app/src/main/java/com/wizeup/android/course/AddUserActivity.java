package com.wizeup.android.course;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.wizeup.android.R;
import com.wizeup.android.model.Response;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Validation.validateEmail;

public class AddUserActivity extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private EditText editTextEmail;
    private String cid;
    private ProgressBar mProgressBar;
    private Button mBtAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        if (!getData()) {
            finish();
        }

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initViews();

    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _cid = getIntent().getExtras().getString("cid");
            if (_cid != null) {
                cid = _cid;
                return true;
            } else
                return false;
        } else
            return false;
    }


    private void initViews() {
        mProgressBar = findViewById(R.id.progress);
        mBtAdd = findViewById(R.id.addUserButton);
        editTextEmail = findViewById(R.id.edit_text_email);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> finish());
        mBtAdd.setOnClickListener(view -> addUser());

    }

    private void addUser() {

        String user = editTextEmail.getText().toString().trim();


        if (!validateEmail(user)) {
            mServerResponse.showSnackBarMessage(getString(R.string.email_should_be_valid));
            return;

        }

        tryToAddProcess(user);
        mBtAdd.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);


    }


    private void tryToAddProcess(String user) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().addStudentToCourse(cid,user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));

    }

    private void handleResponse(Response response) {
        mProgressBar.setVisibility(View.GONE);
        mBtAdd.setVisibility(View.VISIBLE);

        mServerResponse.showSnackBarMessage(response.getMessage());
        editTextEmail.setText("");
    }

    public void handleError(Throwable error) {
        mServerResponse.handleError(error);
        mProgressBar.setVisibility(View.GONE);
        mBtAdd.setVisibility(View.VISIBLE);
    }



}
