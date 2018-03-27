
package com.ariel.wizer.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ariel.wizer.R;
import com.ariel.wizer.SessionActivity;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.network.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Validation.validateFields;

public class ConnectSessionFragment extends Fragment {

    private String pin = "";
    private EditText etClasPin;
    private Button mBtLogin;
    private TextInputLayout mTcalssPin;

    private CompositeSubscription mSubscriptions;

    public static ConnectSessionFragment newInstance() {
        ConnectSessionFragment fragment = new ConnectSessionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_session,container,false);
        mSubscriptions = new CompositeSubscription();

        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mBtLogin = (Button) v.findViewById(R.id.classloginButton);
        etClasPin = (EditText) v.findViewById(R.id.etClas_Pin);
        mTcalssPin = (TextInputLayout) v.findViewById(R.id.tiClasPin);
        mBtLogin.setOnClickListener(view -> login());


    }

    private void setError() {

        mTcalssPin.setError(null);
    }

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
        }
    }

    private void login() {

        setError();

        pin = etClasPin.getText().toString();

        int err = 0;

        if (!validateFields(pin)) {

            err++;
            mTcalssPin.setError("Pin should be valid !");
        }

        if (err == 0) {

            Session session = new Session();
            session.setInternal_id(pin);

            loginProcess(session);

        } else {

            showSnackBarMessage("Enter Valid Details !");
        }
    }

    private void loginProcess(Session session) {
        mSubscriptions.add(NetworkUtil.getRetrofit().connectSession(session)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(Response response) {

//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString(Constants.TOKEN,response.getToken());////////////???
//        editor.putString(Constants.MAIL,response.getMessage());////////////???
//        editor.apply();
//
//        etClasPin.setText(null);
//
//        Intent intent = new Intent(getActivity(), MenuActivity.class);
//        startActivity(intent);

        showSnackBarMessage(response.getMessage());
        Intent intent = new Intent(getActivity(), SessionActivity.class);
        intent.putExtra("pin",pin);
        startActivity(intent);
    }

    private void handleError(Throwable error) {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
