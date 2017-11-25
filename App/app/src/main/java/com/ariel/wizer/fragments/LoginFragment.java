package com.ariel.wizer.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ariel.wizer.MainActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ariel.wizer.ProfileActivity;
import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.NetworkUtil;
import com.ariel.wizer.utils.Constants;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.R.id.langTextView;
import static com.ariel.wizer.utils.Validation.validateEmail;
import static com.ariel.wizer.utils.Validation.validateFields;

public class LoginFragment extends Fragment implements Animation.AnimationListener {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private ImageView mLogoImageView;
    private ImageView mCoverImageView;
    private ImageView mLogoStaticImageView;
    private TextView mLangTextView;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtLogin;
    private Button mNewAccountButton;
    private TextView mTvForgotPassword;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private ProgressBar mProgressBar;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    private static Boolean START_ANIMATION = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        mSubscriptions = new CompositeSubscription();

        initViews(view);
        initSharedPreferences();
        return view;
    }

    private void initViews(View v) {

        mLogoImageView = (ImageView) v.findViewById(R.id.logoImageView);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mCoverImageView = (ImageView) v.findViewById(R.id.coverImageView);
        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mNewAccountButton = (Button) v.findViewById(R.id.newAccountButton);
        mBtLogin = (Button) v.findViewById(R.id.loginButton);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mLangTextView = (TextView) v.findViewById(langTextView);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mTvForgotPassword = (TextView) v.findViewById(R.id.forgotPswTextView);
        mLogoStaticImageView = (ImageView) v.findViewById(R.id.logoStaticImageView);

        mNewAccountButton.setOnClickListener(view -> goToRegister());
        mBtLogin.setOnClickListener(view -> login());
        mTvForgotPassword.setOnClickListener(view -> showDialog());

        if(START_ANIMATION) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                mCoverImageView.setVisibility(View.GONE);
            mEtEmail.setVisibility(View.GONE);
            mEtPassword.setVisibility(View.GONE);
            mLangTextView.setVisibility(View.GONE);
            mTiEmail.setVisibility(View.GONE);
            mTiPassword.setVisibility(View.GONE);
            mTvForgotPassword.setVisibility(View.GONE);
            mNewAccountButton.setVisibility(View.GONE);
            mBtLogin.setVisibility(View.GONE);
            mLogoStaticImageView.setVisibility(View.GONE);

            Animation moveLogoAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.move_logo);
            moveLogoAnimation.setFillAfter(true);
            moveLogoAnimation.setAnimationListener(this);
            mLogoImageView.startAnimation(moveLogoAnimation);
        }
        else {
            mLogoImageView.setVisibility(View.GONE);
        }

        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mEtEmail.getText().toString().length() > 0 && mEtPassword.getText().toString().length() > 0) {
                    mBtLogin.setTextColor(Color.parseColor("#ffffff"));
                    mBtLogin.setEnabled(true);
                }
                else {
                    mBtLogin.setTextColor(Color.parseColor("#aaaaaa"));
                    mBtLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mEtEmail.getText().toString().length() > 0 && mEtPassword.getText().toString().length() > 0) {
                    mBtLogin.setTextColor(Color.parseColor("#ffffff"));
                    mBtLogin.setEnabled(true);
                }
                if(mEtPassword.getText().toString().length() > 0){

                }
                else {
                    mBtLogin.setTextColor(Color.parseColor("#aaaaaa"));
                    mBtLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    private void login() {

        setError();

        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        int err = 0;

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Email should be valid !");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            loginProcess(email,password);
            mProgressBar.setVisibility(View.VISIBLE);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void setError() {

        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void loginProcess(String email, String password) {

        mSubscriptions.add(NetworkUtil.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN,response.getToken());
        editor.putString(Constants.MAIL,response.getMessage());
        editor.apply();

        mEtEmail.setText(null);
        mEtPassword.setText(null);

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);

    }

    private void handleError(Throwable error) {

        mProgressBar.setVisibility(View.GONE);

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goToRegister(){

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        RegisterFragment fragment = new RegisterFragment();
        ft.replace(R.id.fragmentFrame,fragment,RegisterFragment.TAG);
        ft.commit();
    }

    private void showDialog(){

        ResetPasswordDialog fragment = new ResetPasswordDialog();

        fragment.show(getFragmentManager(), ResetPasswordDialog.TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onAnimationStart(Animation animation) {


    }

    @Override
    public void onAnimationEnd(Animation animation) {

        mLogoStaticImageView.setVisibility(View.VISIBLE);
        mLogoImageView.clearAnimation();
        mLogoImageView.setVisibility(View.GONE);

        mEtEmail.setAlpha(0f);
        mEtEmail.setVisibility(View.VISIBLE);
        mEtPassword.setAlpha(0f);
        mEtPassword.setVisibility(View.VISIBLE);
        mLangTextView.setAlpha(0f);
        mLangTextView.setVisibility(View.VISIBLE);
        mTvForgotPassword.setAlpha(0f);
        mTvForgotPassword.setVisibility(View.VISIBLE);
        mNewAccountButton.setAlpha(0f);
        mNewAccountButton.setVisibility(View.VISIBLE);
        mBtLogin.setAlpha(0f);
        mBtLogin.setVisibility(View.VISIBLE);
        mTiEmail.setAlpha(0f);
        mTiEmail.setVisibility(View.VISIBLE);
        mTiPassword.setAlpha(0f);
        mTiPassword.setVisibility(View.VISIBLE);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mCoverImageView.setAlpha(0f);
            mCoverImageView.setVisibility(View.VISIBLE);
        }

        int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mTiEmail.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        mTiPassword.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        mEtEmail.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mEtPassword.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mLangTextView.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        mTvForgotPassword.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        mNewAccountButton.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        mBtLogin.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mCoverImageView.animate()
                    .alpha(1f)
                    .setDuration(mediumAnimationTime)
                    .setListener(null);
        }

        START_ANIMATION = false;

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
