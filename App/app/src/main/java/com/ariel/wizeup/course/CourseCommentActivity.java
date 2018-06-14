package com.ariel.wizeup.course;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.CourseMessage;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.session.SessionCommentsAdapter;
import com.ariel.wizeup.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CourseCommentActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView commentsList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText mCommentText;
    private TextView buttonSend;
    private String cid;
    private String mid;
    private String userId;
    private CourseCommentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_comment);
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
        userId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID, "");
    }

    private void initViews() {
        commentsList = (ListView) findViewById(R.id.comments);
        mCommentText = (EditText) findViewById(R.id.com_text);
        ImageButton buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonSend = (TextView) findViewById(R.id.send_btn);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        buttonSend.setOnClickListener(view -> attemptSendCom());
        buttonBack.setOnClickListener(view -> finish());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _sid = getIntent().getExtras().getString("cid");
            String _mid = getIntent().getExtras().getString("mid");
            if (_sid != null && _mid != null) {
                cid = _sid;
                mid = _mid;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void pullComments() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getCourseMessage(mid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(CourseMessage courseMessages) {
        ArrayList<CourseMessage> saveComments = new ArrayList<>(Arrays.asList(courseMessages.getReplies()));
        saveComments.add(courseMessages);
        Collections.reverse(saveComments);
        mAdapter = new CourseCommentsAdapter(this, new ArrayList<>(saveComments));
        commentsList.setAdapter(mAdapter);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);

    }

    private void attemptSendCom() {
        String strMessage = mCommentText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        CourseMessage message = new CourseMessage();
        message.setMid(mid);
        message.setCid(cid);
        message.setType("answer");
        message.setBody(strMessage);
        sendCom(message);
    }

    private void sendCom(CourseMessage message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishCourseReply(message)
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
