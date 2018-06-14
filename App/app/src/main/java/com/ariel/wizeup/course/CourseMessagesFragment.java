package com.ariel.wizeup.course;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.dialogs.PostBottomDialog;
import com.ariel.wizeup.model.CourseMessage;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.session.CommentActivity;
import com.ariel.wizeup.session.PostActivity;
import com.ariel.wizeup.session.SessionPostsAdapter;
import com.ariel.wizeup.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class CourseMessagesFragment extends android.support.v4.app.Fragment {

    private CompositeSubscription mSubscriptions;
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFBPost;
    private String cid;
    private SessionPostsAdapter mAdapter;
    private String mId;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_messages,container,false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.msg_feed));

        initSharedPreferences();
        getData();
        initViews(view);

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pullMessages();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));

        messagesList.setOnItemClickListener((parent, view1, position, id) -> {
            long viewId = view1.getId();
            if (viewId == R.id.comment_btn) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("cid", cid);
                intent.putExtra("mid", mAdapter.getMessagesList().get(position).get_id());
                startActivity(intent);
            }
        });

        messagesList.setOnScrollListener(new AbsListView.OnScrollListener(){
            private int mLastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    mFBPost.hide();
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    mFBPost.show();
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });
        return view;
    }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID,"");
    }


    private void initViews(View v) {
        mTvNoResults = v.findViewById(R.id.tv_no_results);
        messagesList = v.findViewById(R.id.courseMessagesList);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mFBPost = v.findViewById(R.id.fb_post);
        mFBPost.setOnClickListener(view -> addPost());
    }

    private void addPost(){
//        Intent intent = new Intent(getActivity(), PostActivity.class);
//        intent.putExtra("cid", cid);
//        startActivity(intent);
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cid = bundle.getString("cid");

        }
    }

    private void pullMessages(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().CourseGetAllMessages(cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(CourseMessage courseMessages[]) {
//        if(!(courseMessages.length == 0)){
//            ArrayList<CourseMessage> savePosts = new ArrayList<>(Arrays.asList(courseMessages));
//            Collections.reverse(savePosts);
//            mTvNoResults.setVisibility(View.GONE);
//            mAdapter = new SessionPostsAdapter(this.getActivity(), new ArrayList<>(savePosts));
//
//            messagesList.setAdapter(mAdapter);
//
//        } else {
//            mTvNoResults.setVisibility(View.VISIBLE);
//        }
//
//        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onResume(){
        super.onResume();
        pullMessages();
    }
}
