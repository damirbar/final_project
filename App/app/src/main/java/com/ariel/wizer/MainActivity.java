package com.ariel.wizer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener{

    private ImageView mFbLogoImageView;
    private ImageView mFbCoverImageView;
    private EditText mEmailEditText;
    private EditText mPswEditText;
    private TextView mLangTextView;
    private TextView mForgotPswTextView;
    private Button mLoginButton;
    private Button mNewAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFbLogoImageView = (ImageView) findViewById(R.id.fbLogoImageView);
        mFbCoverImageView = (ImageView) findViewById(R.id.fbCoverImageView);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPswEditText = (EditText) findViewById(R.id.pswEditText);
        mLangTextView = (TextView) findViewById(R.id.langTextView);
        mForgotPswTextView = (TextView) findViewById(R.id.forgotPswTextView);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mNewAccountButton = (Button) findViewById(R.id.newAccountButton);

        mFbCoverImageView.setVisibility(View.GONE);
        mEmailEditText.setVisibility(View.GONE);
        mPswEditText.setVisibility(View.GONE);
        mLangTextView.setVisibility(View.GONE);
        mForgotPswTextView.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.GONE);
        mNewAccountButton.setVisibility(View.GONE);

        Animation moveFBLogoAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_logo);
        moveFBLogoAnimation.setFillAfter(true);
        moveFBLogoAnimation.setAnimationListener(this);
        mFbLogoImageView.startAnimation(moveFBLogoAnimation);


    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        mEmailEditText.setAlpha(0f);
        mEmailEditText.setVisibility(View.VISIBLE);
        mPswEditText.setAlpha(0f);
        mPswEditText.setVisibility(View.VISIBLE);
        mLangTextView.setAlpha(0f);
        mLangTextView.setVisibility(View.VISIBLE);
        mForgotPswTextView.setAlpha(0f);
        mForgotPswTextView.setVisibility(View.VISIBLE);
        mLoginButton.setAlpha(0f);
        mLoginButton.setVisibility(View.VISIBLE);
        mNewAccountButton.setAlpha(0f);
        mNewAccountButton.setVisibility(View.VISIBLE);
        mFbCoverImageView.setAlpha(0f);
        mFbCoverImageView.setVisibility(View.VISIBLE);

        int mediumAnimationTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mEmailEditText.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mPswEditText.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mLangTextView.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mForgotPswTextView.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mLoginButton.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mNewAccountButton.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

        mFbCoverImageView.animate()
                .alpha(1f)
                .setDuration(mediumAnimationTime)
                .setListener(null);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
