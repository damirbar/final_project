package com.wizeup.android.entry;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.base.BaseActivity;
import com.wizeup.android.dialogs.LanguageDialog;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.User;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Constants.EMAIL;
import static com.wizeup.android.utils.Constants.ID;
import static com.wizeup.android.utils.Constants.PASS;
import static com.wizeup.android.utils.Constants.TYPE;
import static com.wizeup.android.utils.Constants.USER_NAME;
import static com.wizeup.android.utils.Validation.validateEmail;

public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText mEtFName;
    private EditText mEtLName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPassword2;
    private Button mBtRegister;
    private ProgressBar mProgressbar;
    private ServerResponse mServerResponse;
    private String mEmail;
    private String mPass;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private RadioButton mRadioButtonStudent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mSubscriptions = new CompositeSubscription();
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.activity_entry));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        initViews(view);
        return view;
    }


    private void initViews(View v) {
        mRadioButtonStudent = v.findViewById(R.id.radio_student);
        mRadioButtonStudent.toggle();
        mEtFName = v.findViewById(R.id.et_first_name);
        mEtLName = v.findViewById(R.id.et_last_name);
        mEtEmail = v.findViewById(R.id.et_email);
        mEtPassword = v.findViewById(R.id.et_password);
        mEtPassword2 = v.findViewById(R.id.et_password2);
        mBtRegister = v.findViewById(R.id.btn_register);
        TextView mTvLogin = v.findViewById(R.id.tv_login);
        mProgressbar = v.findViewById(R.id.progress);
        TextView mLangTextView = v.findViewById(R.id.langTextView);
        mLangTextView.setOnClickListener(view -> askUserLang());
        mBtRegister.setOnClickListener(view -> register());
        mTvLogin.setOnClickListener(view -> goToLogin());
        textChangedListener();

    }

    private void askUserLang() {
        LanguageDialog fragment = new LanguageDialog();
        fragment.show(getFragmentManager(), LanguageDialog.TAG);
    }


    private void register() {
        String first_name = mEtFName.getText().toString().trim();
        String last_name = mEtLName.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String password2 = mEtPassword2.getText().toString().trim();

        if (!validateEmail(email)) {

            showMessage(getString(R.string.email_should_be_valid));
            return;
        }

        if (password.length() < 6) {

            showMessage(getString(R.string.password_must_be_at_least_6_characters));
            return;
        }

        if (!password.equals(password2)) {

            showMessage(getString(R.string.passwords_dont_match));
            return;
        }



        mPass = password;
        mEmail = email;

        User user = new User();
        user.setFname(first_name);
        user.setLname(last_name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPassword_cnfrm(password2);

        if(mRadioButtonStudent.isChecked()){
            user.setRole("student");
        }
        else {
            user.setRole("teacher");
        }

        mBtRegister.setVisibility(View.GONE);
        mProgressbar.setVisibility(View.VISIBLE);

        registerProcess(user);

    }

    private void
    registerProcess(User user) {
        mSubscriptions.add(RetrofitRequests.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(Response response) {
        mProgressbar.setVisibility(View.GONE);
        mBtRegister.setVisibility(View.VISIBLE);
        loginProcess(mEmail, mPass);
    }

    private void handleError(Throwable error) {
        mServerResponse.handleError(error);
        mProgressbar.setVisibility(View.GONE);
        mBtRegister.setVisibility(View.VISIBLE);
    }

    private void showMessage(String message) {

        if (getView() != null) {

            mServerResponse.showSnackBarMessage(message);
        }
    }

    private void goToLogin() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
        ft.commit();
    }

    private void loginProcess(String email, String password) {
        mSubscriptions.add(RetrofitRequests.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLogin, i -> mServerResponse.handleError(i)));
    }


    private void handleResponseLogin(User user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN, user.getToken());
        editor.putString(EMAIL, mEmail);
        editor.putString(PASS, mPass);
        editor.putString(USER_NAME, user.getFname() + " " + user.getLname());
        editor.putString(ID, user.getId_num());
        editor.putString(TYPE,user.getRole());
        editor.apply();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private boolean isComplete() {
        return mEtEmail.getText().toString().length() > 0 &&
                mEtFName.getText().toString().length() > 0 &&
                mEtLName.getText().toString().length() > 0 &&
                mEtPassword.getText().toString().length() > 0 &&
                mEtPassword2.getText().toString().length() > 0;

    }

    private void textChangedListener() {

        mEtFName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isComplete()) {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtRegister.setEnabled(true);
                } else {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
                    mBtRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtLName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isComplete()) {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtRegister.setEnabled(true);
                } else {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
                    mBtRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isComplete()) {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtRegister.setEnabled(true);
                } else {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
                    mBtRegister.setEnabled(false);
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
                if (isComplete()) {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtRegister.setEnabled(true);
                } else {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
                    mBtRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isComplete()) {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape));
                    mBtRegister.setEnabled(true);
                } else {
                    mBtRegister.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.buttonshape_dis));
                    mBtRegister.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    LoginFragment fragment = new LoginFragment();
                    ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
                    ft.commit();

                    return true;

                }

                return false;
            }
        });
    }

}
