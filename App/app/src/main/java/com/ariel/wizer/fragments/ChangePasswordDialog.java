package com.ariel.wizer.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ariel.wizer.network.RetrofitRequests;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.utils.Constants;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Constants.PASS;
import static com.ariel.wizer.utils.Validation.validateFields;

public class ChangePasswordDialog extends DialogFragment {

    public static final String TAG = ChangePasswordDialog.class.getSimpleName();

    private EditText mEtOldPassword;
    private EditText mEtNewPassword;
    private EditText mEtNewPassword2;
    private Button mBtChangePassword;
    private Button mBtCancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldPassword;
    private TextInputLayout mTiNewPassword;
    private TextInputLayout mTiNewPassword2;
    private ProgressBar mProgressBar;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private String mPass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_change_password,container,false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());

        initSharedPreferences();
        initViews(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initViews(View v) {

        mEtOldPassword = (EditText) v.findViewById(R.id.et_old_password);
        mEtNewPassword = (EditText) v.findViewById(R.id.et_new_password);
        mEtNewPassword2 = (EditText) v.findViewById(R.id.et_new_password2);
        mTiOldPassword = (TextInputLayout) v.findViewById(R.id.ti_old_password);
        mTiNewPassword = (TextInputLayout) v.findViewById(R.id.ti_new_password);
        mTiNewPassword2 = (TextInputLayout) v.findViewById(R.id.ti_new_password2);
        mTvMessage = (TextView) v.findViewById(R.id.tv_message);
        mBtChangePassword = (Button) v.findViewById(R.id.btn_change_password);
        mBtCancel = (Button) v.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mBtChangePassword.setOnClickListener(view -> changePassword());
        mBtCancel.setOnClickListener(view -> dismiss());
    }

    private void initSharedPreferences() {
        mPass = mRetrofitRequests.getmSharedPreferences().getString(Constants.PASS,"");
    }


    private void changePassword() {

        setError();

        String oldPassword = mEtOldPassword.getText().toString();
        String newPassword = mEtNewPassword.getText().toString();
        String newPassword2 = mEtNewPassword2.getText().toString();

        int err = 0;

        if (!validateFields(newPassword)) {

            err++;
            mTiNewPassword.setError("Password should not be empty !");
        }

        if (!validateFields(newPassword2)) {

            err++;
            mTiNewPassword2.setError("Password should not be empty !");
        }

        if (!newPassword.equals(newPassword2) && err == 0) {

            err++;
            mTiNewPassword.setError("Passwords don't match !");
            mTiNewPassword2.setError("Passwords don't match !");
        }

        if (!validateFields(oldPassword)) {

            err++;
            mTiOldPassword.setError("Password should not be empty !");
        }

        if (err == 0) {
            mPass = newPassword;
            User user = new User();
            user.setPassword(oldPassword);
            user.setNewPassword(newPassword);
            changePasswordProgress(user);
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void setError() {

        mTiOldPassword.setError(null);
        mTiNewPassword.setError(null);
        mTiNewPassword2.setError(null);
    }

    private void changePasswordProgress(User user) {

        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changePassword(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mRetrofitRequests.getmSharedPreferences().edit();
        editor.putString(PASS,mPass);
        editor.apply();

        dismiss();
    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showMessage("Network Error !");
        }
    }

    private void showMessage(String message) {

        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText(message);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
