

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

import com.ariel.wizer.EditProfileActivity;
import com.ariel.wizer.MainActivity;
import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SideMenuFragment extends Fragment {

    private TextView mTvEmail;
    private TextView mEditProfile;
    private Button mBtLogout;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;

    public static SideMenuFragment newInstance() {
        SideMenuFragment fragment = new SideMenuFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_side_menu,container,false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.activity_nav));

        initViews(view);
        loadProfile();

        return view;
    }


    private void initViews(View v) {
        mEditProfile = (TextView) v.findViewById(R.id.edit_profile);
        mBtLogout = (Button) v.findViewById(R.id.btn_logout);
        mTvEmail = (TextView) v.findViewById(R.id.user_profile_email);
        mBtLogout.setOnClickListener(view -> logout());
        mEditProfile.setOnClickListener(view -> edit());

    }

    private void edit() {
        Intent intent = new Intent(this.getActivity(), EditProfileActivity.class);
        startActivity(intent);
//        getActivity().finish();
    }

    private void loadProfile() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }


    private void handleResponse(User user) {
        mTvEmail.setText(user.getEmail());
    }


    private void logout() {
        SharedPreferences.Editor editor = mRetrofitRequests.getmSharedPreferences().edit();
//        editor.putString(Constants.EMAIL,"");
        editor.putString(Constants.PASS,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
