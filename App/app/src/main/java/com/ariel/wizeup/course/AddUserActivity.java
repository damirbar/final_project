package com.ariel.wizeup.course;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Validation.validateFields;

public class AddUserActivity extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private EditText editTextEmail;
    private String cid;

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
        Button mBtAdd = findViewById(R.id.addUserButton);
        editTextEmail = findViewById(R.id.edit_text_email);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> finish());
        mBtAdd.setOnClickListener(view -> addUser());

    }

    private void addUser() {

        String user = editTextEmail.getText().toString().trim();


        if (!validateFields(user)) {
            mServerResponse.showSnackBarMessage("Email should not be empty.");
            return;

        }

        tryToAddProcess(user);

    }


    private void tryToAddProcess(String user) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().addStudentToCourse(cid,user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));

    }

    private void handleResponse(Response response) {
        mServerResponse.showSnackBarMessage(response.getMessage());
        editTextEmail.setText("");
    }


}
