package com.wizeup.android.course;

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

import com.baoyz.widget.PullRefreshLayout;
import com.wizeup.android.R;
import com.wizeup.android.model.CourseMessage;
import com.wizeup.android.model.Response;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.Constants;
import com.facebook.shimmer.ShimmerFrameLayout;

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
    private PullRefreshLayout mSwipeRefreshLayout;
    private EditText mCommentText;
    private CourseMessage mainMessage;
    private String userId;
    private CourseCommentsAdapter mAdapter;
    private TextView buttonSend;
    private ShimmerFrameLayout mShimmerViewContainer;


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
        commentsList = findViewById(R.id.comments);
        mCommentText = findViewById(R.id.com_text);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonSend = findViewById(R.id.send_btn);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        buttonSend.setOnClickListener(view -> attemptSendCom());
        buttonBack.setOnClickListener(view -> finish());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            CourseMessage _courseMessage = (CourseMessage) getIntent().getExtras().getParcelable("courseMessage");
            if (_courseMessage != null ) {
                mainMessage =_courseMessage;
                return true;
            } else
                return false;
        } else
            return false;
    }

    private void pullComments() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getCourseMessageReplies(mainMessage.get_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(CourseMessage courseMessages[]) {
        ArrayList<CourseMessage> saveComments = new ArrayList<>(Arrays.asList(courseMessages));
        saveComments.add(mainMessage);
        Collections.reverse(saveComments);
        mAdapter = new CourseCommentsAdapter(this, new ArrayList<>(saveComments));
        commentsList.setAdapter(mAdapter);
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);

    }

    private void attemptSendCom() {
        String strMessage = mCommentText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        CourseMessage message = new CourseMessage();
        message.setMid(mainMessage.get_id());
        message.setCid(mainMessage.getCid());
        message.setPoster_id(mainMessage.getPoster_id());
        message.setReplier_id(userId);
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
