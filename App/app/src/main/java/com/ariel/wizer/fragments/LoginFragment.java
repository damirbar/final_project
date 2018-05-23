package com.ariel.wizer.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ariel.wizer.BaseActivity;
import com.ariel.wizer.R;
import com.ariel.wizer.demo.FileDownActivity;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.R.id.langTextView;
import static com.ariel.wizer.utils.Constants.DISPLAY_NAME;
import static com.ariel.wizer.utils.Constants.EMAIL;
import static com.ariel.wizer.utils.Constants.ID;
import static com.ariel.wizer.utils.Constants.PASS;
import static com.ariel.wizer.utils.Constants.PROFILE_IMG;
import static com.ariel.wizer.utils.Constants.TOKEN;
import static com.ariel.wizer.utils.Validation.validateEmail;
import static com.ariel.wizer.utils.Validation.validateFields;

public class LoginFragment extends Fragment{

    public static final String TAG = LoginFragment.class.getSimpleName();

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
    private ServerResponse mServerResponse;
    private String mEmail;
    private String mToken;
    private String mPass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        Intent intent = new Intent(getActivity(),FileDownActivity.class);//remove
//        startActivity(intent);//remove
//        getActivity().finish(); //remove

        initSharedPreferences();
        autoLogin();

        View view = inflater.inflate(R.layout.fragment_login,container,false);
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        mSubscriptions = new CompositeSubscription();
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.activity_main));
        initViews(view);
        rememberEmail();
        return view;
    }

    private void initViews(View v) {

        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mNewAccountButton = (Button) v.findViewById(R.id.newAccountButton);
        mBtLogin = (Button) v.findViewById(R.id.loginButton);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mLangTextView = (TextView) v.findViewById(langTextView);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mTvForgotPassword = (TextView) v.findViewById(R.id.forgotPswTextView);
        mNewAccountButton.setOnClickListener(view -> goToRegister());
        mBtLogin.setOnClickListener(view -> login());
        mTvForgotPassword.setOnClickListener(view -> showDialog());

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
                    mBtLogin.setTextColor(Color.parseColor("#CCCCCC"));
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
                    mBtLogin.setTextColor(Color.parseColor("#CCCCCC"));
                    mBtLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void autoLogin() {
//        if(!mEmail.isEmpty()&&!mPass.isEmpty()){
//            loginProcess(mEmail,mPass);
        if(!mToken.isEmpty()){
        Intent intent = new Intent(getActivity(), BaseActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void rememberEmail() {
        if(!mEmail.isEmpty()){
            mEtEmail.setText(mEmail);
        }
    }


    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");

    }

    private void login() {

        setError();

        mEmail = mEtEmail.getText().toString().trim();
        mPass = mEtPassword.getText().toString().trim();

        int err = 0;

        if (!validateEmail(mEmail)) {

            err++;
            mTiEmail.setError("Email should be valid !");
        }

        if (!validateFields(mPass)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            loginProcess(mEmail,mPass);
            mProgressBar.setVisibility(View.VISIBLE);

        } else {

            showMessage("Enter Valid Details !");
        }
    }

    private void setError() {

        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void loginProcess(String email, String password) {
        mSubscriptions.add(RetrofitRequests.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(User user) {
        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TOKEN,user.getToken());
        editor.putString(EMAIL,user.getEmail());
        editor.putString(PASS,mPass);
        editor.putString(DISPLAY_NAME,user.getFname() + " " + user.getLname());
        editor.putString(ID,user.getId_num());
//        editor.putString(PROFILE_IMG,user.getPhotos()[0]);
        editor.apply();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showMessage(String message) {

        if (getView() != null) {
            mServerResponse.showSnackBarMessage(message);
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
}