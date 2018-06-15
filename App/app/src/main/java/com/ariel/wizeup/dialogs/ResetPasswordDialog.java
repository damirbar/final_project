package com.ariel.wizeup.dialogs;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ariel.wizeup.entry.EntryActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.User;
import com.ariel.wizeup.network.RetrofitRequests;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Constants.PASS;
import static com.ariel.wizeup.utils.Validation.validateEmail;
import static com.ariel.wizeup.utils.Validation.validateFields;

public class ResetPasswordDialog extends DialogFragment {

    public interface Listener {

        void onPasswordReset(String message);
    }

    public static final String TAG = ResetPasswordDialog.class.getSimpleName();

    private EditText mEtEmail;
    private EditText mEtToken;
    private EditText mEtPassword;
    private EditText mEtPassword2;
    private Button mBtResetPassword;
    private TextView mTvMessage;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiToken;
    private TextInputLayout mTiPassword;
    private TextInputLayout mTiPassword2;
    private ProgressBar mProgressBar;

    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;

    private String mEmail;
    private String mPass;
    private boolean isInit = true;

    private Listener mListner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_reset_password,container,false);
        mSubscriptions = new CompositeSubscription();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initViews(view);
        return view;
    }

    private void initViews(View v) {

        mEtEmail = v.findViewById(R.id.et_email);
        mEtToken = v.findViewById(R.id.et_token);
        mEtPassword = v.findViewById(R.id.et_password);
        mEtPassword2 = v.findViewById(R.id.et_password2);
        mBtResetPassword = v.findViewById(R.id.btn_reset_password);
        mProgressBar = v.findViewById(R.id.progress);
        mTvMessage = v.findViewById(R.id.tv_message);
        mTiEmail = v.findViewById(R.id.ti_email);
        mTiToken = v.findViewById(R.id.ti_token);
        mTiPassword = v.findViewById(R.id.ti_password);
        mTiPassword2 = v.findViewById(R.id.ti_password2);


        mBtResetPassword.setOnClickListener(view -> {
            if (isInit) resetPasswordInit();
            else resetPasswordFinish();
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListner = (EntryActivity)context;
    }

    private void setEmptyFields() {

        mTiEmail.setError(null);
        mTiToken.setError(null);
        mTiPassword.setError(null);
        mTiPassword2.setError(null);
        mTvMessage.setText(null);
    }

//    public void setToken(String token) {
//
//        mEtToken.setText(token);
//    }

    private void resetPasswordInit() {

        setEmptyFields();

        mEmail = mEtEmail.getText().toString();

        int err = 0;

        if (!validateEmail(mEmail)) {

            err++;
            mTiEmail.setError(getString(R.string.email_should_be_valid));
        }

        if (err == 0) {

            mProgressBar.setVisibility(View.VISIBLE);
            resetPasswordInitProgress(mEmail);
        }
    }

    private void resetPasswordFinish() {

        setEmptyFields();

        String token = mEtToken.getText().toString();
        String password = mEtPassword.getText().toString();
        String password2 = mEtPassword2.getText().toString();

        int err = 0;

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError(getString(R.string.password_should_not_be_empty));
        }

        if (!validateFields(password2)) {

            err++;
            mTiPassword2.setError(getString(R.string.password_should_not_be_empty));
        }

        if (!password.equals(password2) && err == 0) {

            err++;
            mTiPassword.setError(getString(R.string.passwords_dont_match));
            mTiPassword2.setError(getString(R.string.passwords_dont_match));
        }


        if (!validateFields(token)) {

            err++;
            mTiToken.setError(getString(R.string.token_should_not_be_empty));
        }

        if (err == 0) {

            mProgressBar.setVisibility(View.VISIBLE);
            mPass = password;
            User user = new User();
            user.setEmail(mEmail);
            user.setPassword(password);
            user.setToken(token);
            resetPasswordFinishProgress(user);
        }
    }

    private void resetPasswordInitProgress(String email) {

        mSubscriptions.add(RetrofitRequests.getRetrofit().resetPasswordInit(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void resetPasswordFinishProgress(User user) {

        mSubscriptions.add(RetrofitRequests.getRetrofit().resetPasswordFinish(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        if (isInit) {

            isInit = false;
            showMessage(response.getMessage());
            mTiEmail.setVisibility(View.GONE);
            mTiToken.setVisibility(View.VISIBLE);
            mTiPassword.setVisibility(View.VISIBLE);
            mTiPassword2.setVisibility(View.VISIBLE);


        } else {

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PASS,mPass);
            editor.apply();

            mListner.onPasswordReset(response.getMessage());
            dismiss();
        }
    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showMessage(response.getMessage());

            } catch (JsonSyntaxException | IOException e) {
                e.printStackTrace();
                showMessage(getString(R.string.internal_server_error));
            }
        } else {

            showMessage("No Internet Connection.");
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
