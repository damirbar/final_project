package com.ariel.wizeup.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.User;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Constants.PASS;
import static com.ariel.wizeup.utils.Validation.validateFields;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText mEtOldPassword;
    private EditText mEtNewPassword;
    private EditText mEtNewPassword2;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll));
        initViews();

    }

    private void initViews() {
        Button mBSave = findViewById(R.id.save_button);
        Button mBCancel = findViewById(R.id.cancel_button);
        mEtOldPassword = findViewById(R.id.et_old_password);
        mEtNewPassword = findViewById(R.id.et_new_password);
        mEtNewPassword2 = findViewById(R.id.et_new_password2);
        mBSave.setOnClickListener(view -> changePassword());
        mBCancel.setOnClickListener(view -> finish());

    }

    private void changePassword() {

        String oldPassword = mEtOldPassword.getText().toString();
        String newPassword = mEtNewPassword.getText().toString();
        String newPassword2 = mEtNewPassword2.getText().toString();


        if (!validateFields(newPassword)) {

            mServerResponse.showSnackBarMessage(getString(R.string.password_should_not_be_empty));
            return;
        }

        else if (!validateFields(newPassword2)) {

            mServerResponse.showSnackBarMessage(getString(R.string.password_should_not_be_empty));
            return;

        }

        else if (!newPassword.equals(newPassword2)) {

            mServerResponse.showSnackBarMessage(getString(R.string.passwords_dont_match));
            return;

        }

        else if (!validateFields(oldPassword)) {

            mServerResponse.showSnackBarMessage(getString(R.string.password_should_not_be_empty));
            return;

        }

        else if (newPassword2.length() < 6 || newPassword.length() < 6) {

            mServerResponse.showSnackBarMessage(getString(R.string.password_must_be_at_least_6_characters));
            return;

        }


        mPass = newPassword;
            User user = new User();
            user.setPassword(oldPassword);
            user.setNewPassword(newPassword);
            changePasswordProgress(user);

    }

    private void changePasswordProgress(User user) {

        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changePassword(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }


    private void handleResponse(Response response) {
//        mServerResponse.showSnackBarMessage(response.getMessage());
        SharedPreferences.Editor editor = mRetrofitRequests.getSharedPreferences().edit();
        editor.putString(PASS,mPass);
        editor.apply();
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
