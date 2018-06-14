package com.ariel.wizeup.course;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.dialogs.UploadingDialog;
import com.ariel.wizeup.model.CourseFile;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static com.ariel.wizeup.network.RetrofitRequests.getBytes;

public class SessionFeedFragment extends Fragment {

    private CompositeSubscription mSubscriptions;
    private ListView sessionsList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFBAddSession;
    private SessionsAdapter mAdapter;
    private String cid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_feed, container, false);
        getData();
        initViews(view);

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.activity_sessions_feed));

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pullSessions();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));

        sessionsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    mFBAddSession.hide();
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    mFBAddSession.show();
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });
        pullSessions();
        return view;
    }

    private void initViews(View v) {
        mTvNoResults = v.findViewById(R.id.tv_no_results);
        sessionsList = v.findViewById(R.id.sessions_list);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mFBAddSession = v.findViewById(R.id.fb_add_session);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mFBAddSession.setOnClickListener(view -> addSession());
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cid = bundle.getString("cid");
        }
    }

    private void addSession() {
        Intent intent = new Intent(getActivity(),CourseSessionActivity.class);
        intent.putExtra("cid", cid);
        startActivity(intent);

    }


    private void pullSessions() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getCourseSessions(cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(Session sessions[]) {
        if (!(sessions.length == 0)) {
            ArrayList<Session> saveSessions = new ArrayList<>(Arrays.asList(sessions));
            Collections.reverse(saveSessions);
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SessionsAdapter(this.getActivity(), new ArrayList<>(saveSessions));
            sessionsList.setAdapter(mAdapter);
        } else {
            mTvNoResults.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}

