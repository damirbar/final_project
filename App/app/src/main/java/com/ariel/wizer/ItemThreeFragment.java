
package com.ariel.wizer;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ariel.wizer.fragments.ChangePasswordDialog;
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

public class ItemThreeFragment extends Fragment implements ChangePasswordDialog.Listener {

    public static final String TAG = ItemThreeFragment.class.getSimpleName();

    private TextView mTvName;
    private TextView mTvEmail;
    private TextView mTvDate;
    private Button mBtChangePassword;
    private Button mBtLogout;

    private ProgressBar mProgressbar;

    //private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;

    private CompositeSubscription mSubscriptions;


    public static ItemThreeFragment newInstance() {
        ItemThreeFragment fragment = new ItemThreeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString("params");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_three, container, false);
        mSubscriptions = new CompositeSubscription();
        //getData();
        initViews(view);
        //initSharedPreferences();
        loadProfile();
        return view;
    }
    private void initViews(View v) {

        mTvName = (TextView) v.findViewById(R.id.tv_name);
        mTvEmail = (TextView) v.findViewById(R.id.tv_email);
        mTvDate = (TextView) v.findViewById(R.id.tv_date);
        mBtChangePassword = (Button) v.findViewById(R.id.btn_change_password);
        mBtLogout = (Button) v.findViewById(R.id.btn_logout);
        mProgressbar = (ProgressBar) v.findViewById(R.id.progress);

        mBtChangePassword.setOnClickListener(view -> showDialog());
        mBtLogout.setOnClickListener(view -> logout());
    }

    private void initSharedPreferences() {

//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
//        mEmail = mSharedPreferences.getString(Constants.MAIL,"");
    }

    private void getData() {

        Bundle bundle = getArguments();

        mEmail = bundle.getString(Constants.MAIL);
    }

    private void logout() {

        //SharedPreferences.Editor editor = mSharedPreferences.edit();
        //editor.putString(Constants.MAIL,"");
        //editor.putString(Constants.TOKEN,"");
        //editor.apply();
        //finish();
    }

    private void showDialog(){

//        ChangePasswordDialog fragment = new ChangePasswordDialog();
//
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.MAIL, mEmail);
//        bundle.putString(Constants.TOKEN,mToken);
//        fragment.setArguments(bundle);

        //fragment.show(getFragmentManager(), ChangePasswordDialog.TAG);
    }

    private void loadProfile() {

        mTvEmail.setText(mEmail);


//        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {

//        mProgressbar.setVisibility(View.GONE);
//        mTvName.setText(user.getDisplayName());
//        mTvEmail.setText(user.getMail());
//        mTvDate.setText(user.getRegisterDate());
    }

    private void handleError(Throwable error) {

//        mProgressbar.setVisibility(View.GONE);
//
//        if (error instanceof HttpException) {
//
//            Gson gson = new GsonBuilder().create();
//
//            try {
//
//                String errorBody = ((HttpException) error).response().errorBody().string();
//                Response response = gson.fromJson(errorBody,Response.class);
//                showSnackBarMessage(response.getMessage());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//
//            showSnackBarMessage("Network Error !");
//        }
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

    @Override
    public void onPasswordChanged() {

        showSnackBarMessage("Password Changed Successfully !");
    }
}


