package com.ariel.wizeup.session;

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
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionMessagesFragment extends android.support.v4.app.Fragment {

    private CompositeSubscription mSubscriptions;
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFBPost;
    private String sid;
    private SessionPostsAdapter mAdapter;
    private String mId;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_session_posts,container,false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.activity_session_feed));

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
                intent.putExtra("sid", sid);
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
        messagesList = v.findViewById(R.id.sessionMessagesList);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mFBPost = v.findViewById(R.id.fb_post);
        mFBPost.setOnClickListener(view -> askUser());
    }

    private void askUser(){
        Bundle bundle = new Bundle();
        bundle.putString("sid", sid);
        PostBottomDialog tab1 = new PostBottomDialog();
        tab1.setArguments(bundle);
        tab1.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");

        }
    }

    private void pullMessages(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getAllMessages(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(SessionMessage sessionMessages[]) {
        if(!(sessionMessages.length == 0)){
            ArrayList<SessionMessage> savePosts = new ArrayList<>(Arrays.asList(sessionMessages));
            Collections.reverse(savePosts);
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SessionPostsAdapter(this.getActivity(), new ArrayList<>(savePosts));

            for(int i=0;i<mAdapter.getMessagesList().size();i++){
                for(int j=0;j<mAdapter.getMessagesList().get(i).getLikers().length;j++){
                    if(mAdapter.getMessagesList().get(i).getLikers()[j].equalsIgnoreCase(mId)) {
                        mAdapter.getLikeCheckBoxState()[i] = true;
                    }
                }
                for(int j=0;j<mAdapter.getMessagesList().get(i).getDislikers().length;j++){
                    if(mAdapter.getMessagesList().get(i).getDislikers()[j].equalsIgnoreCase(mId)) {
                        mAdapter.getDislikeCheckBoxState()[i] = true;
                    }
                }
            }
            messagesList.setAdapter(mAdapter);

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

    @Override
    public void onResume(){
        super.onResume();
        pullMessages();
    }
}
