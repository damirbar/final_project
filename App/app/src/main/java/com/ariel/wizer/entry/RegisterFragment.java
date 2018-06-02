package com.ariel.wizer.entry;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.base.BaseActivity;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Constants.EMAIL;
import static com.ariel.wizer.utils.Constants.ID;
import static com.ariel.wizer.utils.Constants.PASS;
import static com.ariel.wizer.utils.Constants.USER_NAME;
import static com.ariel.wizer.utils.Validation.validateEmail;
import static com.ariel.wizer.utils.Validation.validateFields;

public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText mEtFName;
    private EditText mEtLName;
    private EditText mEtEmail;
    private EditText mEtPassword;
    private EditText mEtPassword2;
    private Button mBtRegister;
    private Spinner mTypeSpinner;

    private TextView mTvLogin;
    private TextInputLayout mTiFName;
    private TextInputLayout mTiLName;
    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private TextInputLayout mTiPassword2;
    private ProgressBar mProgressbar;
    private ServerResponse mServerResponse;
    private String mEmail;
    private String mPass;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);
        mSubscriptions = new CompositeSubscription();
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.activity_main));
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        initViews(view);
        return view;
    }

    private void initViews(View v) {

        mTypeSpinner = (Spinner) v.findViewById(R.id.type_spinner);
        mEtFName = (EditText) v.findViewById(R.id.et_first_name);
        mEtLName = (EditText) v.findViewById(R.id.et_last_name);
        mEtEmail = (EditText) v.findViewById(R.id.et_email);
        mEtPassword = (EditText) v.findViewById(R.id.et_password);
        mEtPassword2 = (EditText) v.findViewById(R.id.et_password2);
        mBtRegister = (Button) v.findViewById(R.id.btn_register);
        mTvLogin = (TextView) v.findViewById(R.id.tv_login);
        mTiFName = (TextInputLayout) v.findViewById(R.id.ti_first_name);
        mTiLName = (TextInputLayout) v.findViewById(R.id.ti_last_name);
        mTiEmail = (TextInputLayout) v.findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) v.findViewById(R.id.ti_password);
        mTiPassword2 = (TextInputLayout) v.findViewById(R.id.ti_password2);
        mProgressbar = (ProgressBar) v.findViewById(R.id.progress);

        mBtRegister.setOnClickListener(view -> register());
        mTvLogin.setOnClickListener(view -> goToLogin());
    }

    private void register() {

        setError();

        String first_name = mEtFName.getText().toString().trim();
        String last_name = mEtLName.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String password2 = mEtPassword2.getText().toString().trim();

        int err = 0;

        if (password.length()<6) {

            err++;
            mTiPassword.setError("Password must be at least 6 characters.");
            mTiPassword2.setError("Password must be at least 6 characters.");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty.");
        }

        if (!validateFields(password2)) {

            err++;
            mTiPassword2.setError("Password should not be empty.");
        }

        if (!password.equals(password2) && err == 0) {

            err++;
            mTiPassword.setError("Passwords don't match.");
            mTiPassword2.setError("Passwords don't match.");
        }



        if (!validateFields(first_name)) {

            err++;
            mTiFName.setError("First Name should not be empty.");
        }

        if (!validateFields(last_name)) {

            err++;
            mTiLName.setError("Last Name should not be empty.");
        }

        if (!validateEmail(email)) {

            err++;
            mTiEmail.setError("Email should be valid.");
        }


        if (err == 0) {

            mPass = password;
            mEmail = email;

            User user = new User();
            user.setFname(first_name);
            user.setLname(last_name);
            user.setEmail(email);
            user.setPassword(password);

            mProgressbar.setVisibility(View.VISIBLE);
            registerProcess(user);

        }
    }

    private void setError() {
        mTiFName.setError(null);
        mTiLName.setError(null);
        mTiEmail.setError(null);
        mTiPassword.setError(null);
        mTiPassword2.setError(null);
    }

    private void
    registerProcess(User user) {
        mSubscriptions.add(RetrofitRequests.getRetrofit().register(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {
        mProgressbar.setVisibility(View.GONE);
        loginProcess(mEmail,mPass);
    }

    private void handleError(Throwable error) {
        mServerResponse.handleError(error);
        mProgressbar.setVisibility(View.GONE);
    }

    private void showMessage(String message) {

        if (getView() != null) {

            mServerResponse.showSnackBarMessage(message);
        }
    }

    private void goToLogin(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragmentFrame, fragment, LoginFragment.TAG);
        ft.commit();
    }

    private void loginProcess(String email, String password) {
        mSubscriptions.add(RetrofitRequests.getRetrofit(email, password).login()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseLogin,i -> mServerResponse.handleError(i)));
    }


    private void handleResponseLogin(User user) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.TOKEN,user.getToken());
        editor.putString(EMAIL,mEmail);
        editor.putString(PASS,mPass);
        editor.putString(USER_NAME,user.getFname() + " " + user.getLname());
        editor.putString(ID,user.getId_num());
//        editor.putString(PROFILE_IMG,user.getPhotos()[0]);
        editor.apply();

        Intent intent = new Intent(getActivity(), BaseActivity.class);
        startActivity(intent);
        getActivity().finish();
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

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

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
