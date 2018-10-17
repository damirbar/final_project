package com.ariel.wizeup.session;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.utils.Constants;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionCommentActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView commentsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText mCommentText;
    private TextView buttonSend;
    private String userId;
    private SessionCommentsAdapter mAdapter;
    private SessionMessage mainMessage;
    private String nickname;
    private ShimmerFrameLayout mShimmerViewContainer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_comment);
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

        mCommentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mCommentText.getText().toString().length() > 0) {
                    buttonSend.setTextColor(getApplication().getResources().getColor(R.color.black));
                }
                else {
                    buttonSend.setTextColor(getApplication().getResources().getColor(R.color.def_text));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initSharedPreferences() {
        userId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID, "");
    }

    private void initViews() {
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        commentsList = (ListView) findViewById(R.id.comments);
        mCommentText = (EditText) findViewById(R.id.com_text);
        ImageButton buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonSend = (TextView) findViewById(R.id.send_btn);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        buttonSend.setOnClickListener(view -> attemptSendCom());
        buttonBack.setOnClickListener(view -> finish());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            SessionMessage _SessionMessage = (SessionMessage) getIntent().getExtras().getParcelable("sessionMessage");
            String _nickname =  getIntent().getExtras().getString("nickname");
            if (_SessionMessage != null && _nickname != null) {
                mainMessage = _SessionMessage;
                nickname = _nickname;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void pullComments() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getSessionMessageReplies(mainMessage.get_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(SessionMessage sessionMessages[]) {
        ArrayList<SessionMessage> saveComments = new ArrayList<>(Arrays.asList(sessionMessages));
        saveComments.add(mainMessage);
        Collections.reverse(saveComments);
        mAdapter = new SessionCommentsAdapter(this, new ArrayList<>(saveComments));
        for (int i = 0; i < mAdapter.getMessagesList().size(); i++) {
            for (int j = 0; j < mAdapter.getMessagesList().get(i).getLikers().length; j++) {
                if (mAdapter.getMessagesList().get(i).getLikers()[j].equalsIgnoreCase(userId)) {
                    mAdapter.getLikeCheckBoxState()[i] = true;
                }
            }
            for (int j = 0; j < mAdapter.getMessagesList().get(i).getDislikers().length; j++) {
                if (mAdapter.getMessagesList().get(i).getDislikers()[j].equalsIgnoreCase(userId)) {
                    mAdapter.getDislikeCheckBoxState()[i] = true;
                }
            }
        }
        commentsList.setAdapter(mAdapter);
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    private void attemptSendCom() {
        String strMessage = mCommentText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        SessionMessage message = new SessionMessage();
        message.setMid(mainMessage.get_id());
        message.setSid(mainMessage.getSid());
        message.setPoster_id(mainMessage.getPoster_id());
        message.setReplier_id(userId);
        message.setType("answer");
        message.setBody(strMessage);
        message.setNickname(nickname);
        sendCom(message);
    }

    private void sendCom(SessionMessage message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishReply(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendCom, i -> mServerResponse.handleError(i)));
    }

    private void handleResponseSendCom(Response response) {
        mCommentText.setText("");
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
