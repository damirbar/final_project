
package com.ariel.wizer.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ariel.wizer.MainActivity;
import com.ariel.wizer.NavBarActivity;
import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.NetworkUtil;
import com.ariel.wizer.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ProfileFragment extends Fragment  {

    private TextView mTvName;
    private TextView mTvEmail;
    private Button mBtChangePassword;
    private Button mBtLogout;

    private SharedPreferences mSharedPreferences;
    private String mToken;

    private CompositeSubscription mSubscriptions;


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pofile, container, false);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        initSharedPreferences();
        loadProfile();
        return view;
    }
    private void initViews(View v) {

        mTvName = (TextView) v.findViewById(R.id.tv_name);
        mTvEmail = (TextView) v.findViewById(R.id.tv_email);
        mBtChangePassword = (Button) v.findViewById(R.id.btn_change_password);
        mBtLogout = (Button) v.findViewById(R.id.btn_logout);

        mBtChangePassword.setOnClickListener(view -> showDialog());
        mBtLogout.setOnClickListener(view -> logout());
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
    }


    private void logout() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.PASS,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

        getActivity().finish();
    }

    private void showDialog(){

        ChangePasswordDialog fragment = new ChangePasswordDialog();
        fragment.show(getActivity().getFragmentManager(), ChangePasswordDialog.TAG);
    }

    private void loadProfile() {
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        mTvName.setText(user.getFname());
        mTvEmail.setText(user.getEmail());
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

    private void showSnackBarMessage(String message) {

        if (getView() != null) {

            Snackbar.make(getView(),message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}


