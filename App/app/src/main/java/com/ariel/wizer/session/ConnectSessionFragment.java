
package com.ariel.wizer.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Validation.validateFields;

public class ConnectSessionFragment extends Fragment {

    private String sid;
    private EditText mEditTextSid;
    private Button mBtLogin;
    private Button mCreateSessionButton;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;


    public static ConnectSessionFragment newInstance() {
        ConnectSessionFragment fragment = new ConnectSessionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_session,container,false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.activity_nav));

        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mBtLogin = (Button) v.findViewById(R.id.classloginButton);
        mCreateSessionButton = (Button) v.findViewById(R.id.create_session_button);
        mEditTextSid = (EditText) v.findViewById(R.id.edit_text_sid);
        mBtLogin.setOnClickListener(view -> login());
        mCreateSessionButton.setOnClickListener(view -> createSession());

    }

    private void createSession() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().createSession()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCreateSession,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseCreateSession(Session session) {
        sid = session.getSid();
        Intent intent = new Intent(getActivity().getBaseContext(), SessionActivity.class);
        intent.putExtra("sid",sid);
        getActivity().startActivity(intent);
    }


    private void setError() {
        mEditTextSid.setError(null);
    }


    private void login() {

        setError();

        sid = mEditTextSid.getText().toString().trim();

        int err = 0;

        if (!validateFields(sid)) {

            err++;
            mEditTextSid.setError("Session should be valid !");

        }

        if (err == 0) {
            Session session = new Session();
            session.setSid(sid);
            loginProcess(session);
        }
    }

    private void loginProcess(Session session) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().connectSession(session)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Response response) {
        Intent intent = new Intent(getActivity().getBaseContext(), SessionActivity.class);
        intent.putExtra("sid",sid);
        getActivity().startActivity(intent);
        mEditTextSid.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
