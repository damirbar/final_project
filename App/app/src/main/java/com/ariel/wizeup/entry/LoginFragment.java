package com.ariel.wizeup.entry;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.base.BaseActivity;
import com.ariel.wizeup.dialogs.ResetPasswordDialog;
import com.ariel.wizeup.model.User;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.settings.ChangeLanguage;
import com.ariel.wizeup.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Constants.EMAIL;
import static com.ariel.wizeup.utils.Constants.ID;
import static com.ariel.wizeup.utils.Constants.PASS;
import static com.ariel.wizeup.utils.Constants.PROFILE_IMG;
import static com.ariel.wizeup.utils.Constants.TOKEN;
import static com.ariel.wizeup.utils.Constants.USER_NAME;
import static com.ariel.wizeup.utils.Validation.validateEmail;
import static com.ariel.wizeup.utils.Validation.validateFields;

public class LoginFragment extends Fragment{

    public static final String TAG = LoginFragment.class.getSimpleName();

    private EditText mEtEmail;
    private EditText mEtPassword;
    private Button mBtLogin;
    private ProgressBar mProgressBar;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private ServerResponse mServerResponse;
    private String mEmail;
    private String mToken;
    private String mPass;
    private ChangeLanguage changeLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent intent = new Intent(getActivity(),FileUploadActivity.class);//remove
//        startActivity(intent);//remove
//        getActivity().finish(); //remove

        changeLanguage =new ChangeLanguage(getActivity());
        initSharedPreferences();
        autoLogin();
        View view = inflater.inflate(R.layout.fragment_login,container,false);
        mSubscriptions = new CompositeSubscription();
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.activity_entry));
        initViews(view);
        rememberEmail();
        return view;
    }

    private void initViews(View v) {

        mEtEmail = v.findViewById(R.id.et_email);
        mEtPassword = v.findViewById(R.id.et_password);
        Button mNewAccountButton = v.findViewById(R.id.newAccountButton);
        mBtLogin = v.findViewById(R.id.loginButton);
        TextView mLangTextView = v.findViewById(R.id.langTextView);
        mProgressBar = v.findViewById(R.id.progress);
        TextView mTvForgotPassword = v.findViewById(R.id.forgotPswTextView);
        mNewAccountButton.setOnClickListener(view -> goToRegister());
        mBtLogin.setOnClickListener(view -> login());
        mTvForgotPassword.setOnClickListener(view -> resetPassDialog());
        mLangTextView.setOnClickListener(view -> changeLanguage.changeLanguageDialog());


        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mEtEmail.getText().toString().length() > 0 && mEtPassword.getText().toString().length() > 0) {
                    mBtLogin.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtLogin.setEnabled(true);
                }
                else {
                    mBtLogin.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
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
                    mBtLogin.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtLogin.setEnabled(true);
                }
                else {
                    mBtLogin.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
                    mBtLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void autoLogin() {
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
        String lang = mSharedPreferences.getString(Constants.LANG,"");
        changeLanguage.setLocale(lang);
    }

    private void login() {


        mEmail = mEtEmail.getText().toString().trim();
        mPass = mEtPassword.getText().toString().trim();


        if (!validateEmail(mEmail)) {

            showMessage(getString(R.string.email_should_be_valid));
            return;

        }

            loginProcess(mEmail,mPass);
            mProgressBar.setVisibility(View.VISIBLE);

    }


    private void loginProcess(String email, String password) {
        mSubscriptions.add(RetrofitRequests.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        mProgressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TOKEN,user.getToken());
        editor.putString(EMAIL,user.getEmail());
        editor.putString(PASS,mPass);
        editor.putString(USER_NAME,user.getFname() + " " + user.getLname());
        editor.putString(ID,user.getId_num());
        editor.putString(PROFILE_IMG,user.getPhotos()[0]);
        editor.apply();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void handleError(Throwable error) {
      mServerResponse.handleError(error);
      mProgressBar.setVisibility(View.GONE);
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

    private void resetPassDialog(){
        ResetPasswordDialog fragment = new ResetPasswordDialog();
        fragment.show(getFragmentManager(), ResetPasswordDialog.TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}