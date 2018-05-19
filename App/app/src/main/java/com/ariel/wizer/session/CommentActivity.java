package com.ariel.wizer.session;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CommentActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView commentsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private EditText mComtText;
    private ImageButton buttonBack;
    private TextView buttonSend;
    private final String answer = "answer";
    private String sid;
    private String msid;

    private SessionCommentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_comment));

        if (!getData()) {
            finish();
        }
        initViews();

//        SessionMessage msg = (SessionMessage) getIntent().getSerializableExtra("msg");

        pullComments();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullComments();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void initViews() {
        commentsList = (ListView) findViewById(R.id.comments);
        mComtText = (EditText) findViewById(R.id.com_text);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonSend = (TextView) findViewById(R.id.send_btn);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        buttonSend.setOnClickListener(view -> attemptSendCom());
        buttonBack.setOnClickListener(view -> goBack());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _sid = getIntent().getExtras().getString("sid");
            String _msid = getIntent().getExtras().getString("msid");
            if(_sid != null||_msid != null) {
                sid = _sid;
                msid = _msid;
                return true;
            } else
                return false;
        }
        else
            return false;
    }

    private void pullComments(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessages(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(SessionMessage sessionMessages[]) {
        if(! (sessionMessages.length == 0)){
            ArrayList<SessionMessage> saveComments = new ArrayList<>(Arrays.asList(sessionMessages));
            Collections.reverse(saveComments);
            mAdapter = new SessionCommentsAdapter(this, new ArrayList<>(saveComments));
            commentsList.setAdapter(mAdapter);
        }
    }

    private void goBack() {
        finish();
    }

    private void attemptSendCom() {
        String strMessage = mComtText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        SessionMessage message = new  SessionMessage();
        message.setSid(sid);
        message.setType(answer);
        String Body[]={"",strMessage};
        message.setBody(Body);
        sendCom(message);
    }

    private void sendCom(SessionMessage  message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishSessionMessage(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendCom,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseSendCom(Response response) {
        mComtText.setText("");
        pullComments(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        pullComments();
    }

}
