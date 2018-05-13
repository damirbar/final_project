
package com.ariel.wizer.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ariel.wizer.R;
import com.ariel.wizer.utils.Constants;
import com.arlib.floatingsearchview.FloatingSearchView;

import rx.subscriptions.CompositeSubscription;

public class HomeFragment extends Fragment {

    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String mToken;

    FloatingSearchView mSearchView;




    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        initViews(view);

//        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
//            @Override
//            public void onSearchTextChanged(String oldQuery, final String newQuery) {
//
//                //get suggestions based on newQuery
//
//                //pass them on to the search view
//                //mSearchView.swapSuggestions(newSuggestions);
//            }
//        });
        return view;
    }

    private void initViews(View v) {
        //mSearchView = (com.arlib.floatingsearchview.FloatingSearchView) v.findViewById(R.floating_search_view);
    }

    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
    }


//    private void setError() {
//        mTcalssPin.setError(null);
//    }
//
//    private void showSnackBarMessage(String message) {
//
//        if (getView() != null) {
//
//            Snackbar.make(getView(),message,Snackbar.LENGTH_SHORT).show();
//        }
//    }
//
//    private void serch() {
//
//        setError();
//
//        pin = etClasPin.getText().toString();
//
//        int err = 0;
//
//        if (!validateFields(pin)) {
//
//            err++;
//            mTcalssPin.setError("Pin should be valid !");
//
//        }
//
//        if (err == 0) {
//            loginProcess(pin);
//        }
//    }

//    private void loginProcess(String id) {
//        mSubscriptions.add(RetrofitRequests.getRetrofit(mToken).connectSession(id)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponse,this::handleError));
//    }
//
//    private void handleResponse(Response response) {
//        showSnackBarMessage(response.getMessage());
//        Intent intent = new Intent(getActivity(), SessionActivity.class);
//        intent.putExtra("pin",pin);
//        startActivity(intent);
//    }
//
//    private void handleError(Throwable error) {
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
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
