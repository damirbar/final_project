package com.ariel.wizeup.course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.session.ConnectSessionActivity;
import com.ariel.wizeup.utils.Constants;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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
    private String userType;
    private ShimmerFrameLayout mShimmerViewContainer;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_feed, container, false);
        getData();
        initViews(view);
        initSharedPreferences();
        userAdmin();

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.activity_sessions_feed));

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pullSessions();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));

        sessionsList.setOnItemClickListener((parent, view1, position, id) -> {
            Session session = mAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ConnectSessionActivity.class);
            intent.putExtra("sid", session.getSid());
            startActivity(intent);
        });


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
        mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();
        mTvNoResults = v.findViewById(R.id.tv_no_results);
        sessionsList = v.findViewById(R.id.sessions_list);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mFBAddSession = v.findViewById(R.id.fb_add_session);
        mFBAddSession.setOnClickListener(view -> addSession());
    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userType = mSharedPreferences.getString(Constants.TYPE, "");
    }

    private void userAdmin() {
        if(!userType.equalsIgnoreCase("teacher")){
            mFBAddSession.setVisibility(View.GONE);
        }
    }


    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cid = bundle.getString("cid");
        }
    }

    private void addSession() {
        Intent intent = new Intent(getActivity(), CourseSessionActivity.class);
        intent.putExtra("cid", cid);
        startActivity(intent);

    }


    private void pullSessions() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getCourseSessions(cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
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
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onResume(){
        super.onResume();
        pullSessions();
    }

}

