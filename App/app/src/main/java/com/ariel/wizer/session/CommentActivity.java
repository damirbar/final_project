package com.ariel.wizer.session;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

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
    private String sid;
    private String mid;
    private String mId;
    private SessionCommentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_comment));
        initSharedPreferences();
        if (!getData()) {
            finish();
        }
        initViews();

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pullComments();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));
    }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID, "");
    }

    private void initViews() {
        commentsList = (ListView) findViewById(R.id.comments);
        mComtText = (EditText) findViewById(R.id.com_text);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonSend = (TextView) findViewById(R.id.send_btn);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        buttonSend.setOnClickListener(view -> attemptSendCom());
        buttonBack.setOnClickListener(view -> finish());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _sid = getIntent().getExtras().getString("sid");
            String _mid = getIntent().getExtras().getString("mid");
            if (_sid != null && _mid != null) {
                sid = _sid;
                mid = _mid;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void pullComments() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessage(mid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(SessionMessage sessionMessages) {
        ArrayList<SessionMessage> saveComments = new ArrayList<>(Arrays.asList(sessionMessages.getReplies()));
        saveComments.add(sessionMessages);
        Collections.reverse(saveComments);
        mAdapter = new SessionCommentsAdapter(this, new ArrayList<>(saveComments));
        for (int i = 0; i < mAdapter.getMessagesList().size(); i++) {
            for (int j = 0; j < mAdapter.getMessagesList().get(i).getLikers().length; j++) {
                if (mAdapter.getMessagesList().get(i).getLikers()[j].equalsIgnoreCase(mId)) {
                    mAdapter.getLikeCheckBoxState()[i] = true;
                }
            }
            for (int j = 0; j < mAdapter.getMessagesList().get(i).getDislikers().length; j++) {
                if (mAdapter.getMessagesList().get(i).getDislikers()[j].equalsIgnoreCase(mId)) {
                    mAdapter.getDislikeCheckBoxState()[i] = true;
                }
            }
        }
        commentsList.setAdapter(mAdapter);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);

    }

    private void attemptSendCom() {
        String strMessage = mComtText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        SessionMessage message = new SessionMessage();
        message.setMid(mid);
        message.setSid(sid);
        message.setType("answer");
        String Body[] = {"", strMessage};
        message.setBody(Body);
        sendCom(message);
    }

    private void sendCom(SessionMessage message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishReply(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendCom, i -> mServerResponse.handleError(i)));
    }

    private void handleResponseSendCom(Response response) {
        mComtText.setText("");
        pullComments();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pullComments();
    }

}
