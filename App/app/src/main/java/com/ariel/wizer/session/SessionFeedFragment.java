package com.ariel.wizer.session;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
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

public class SessionFeedFragment extends android.support.v4.app.Fragment {

    private CompositeSubscription mSubscriptions;
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFBPost;
    private String sid;
    private SessionPostsAdapter mAdapter;
//    private final int delay = 5000; //milliseconds


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_feed,container,false);
        getData();
        initViews(view);
//        mSocket.on(Socket.EVENT_CONNECT,onConnect);
//        mSocket.connect();

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.main_layout));
        pullMessages();
//        classAvgProcess();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                classAvgProcess();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullMessages();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        messagesList.setOnItemClickListener((parent, view1, position, id) -> {
            long viewId = view1.getId();
            if (viewId == R.id.comment_btn) {
//                SessionMessage msg = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("sid", sid);
                intent.putExtra("msid", mAdapter.getMessagesList().get(position).get_id());
                startActivity(intent);
            }
        });

        return view;
    }

    private void initViews(View v) {
//        mTvClassAvg = (TextView) findViewById(R.id.tVclassAvg);
        mTvNoResults = (TextView) v.findViewById(R.id.tv_no_results);
        messagesList = (ListView) v.findViewById(R.id.sessionMessagesList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mFBPost = (FloatingActionButton) v.findViewById(R.id.fb_post);
        mFBPost.setOnClickListener(view -> openPost());
    }


//    private Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket(Constants.BASE_URL);
//        } catch (URISyntaxException e) {}
//    }



    private void openPost(){
        Intent intent = new Intent(getActivity(), PostActivity.class);
        intent.putExtra("sid",sid);
        startActivity(intent);
    }


    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
        }

    }

//    private void classAvgProcess() {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsCount(pin)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseAvg,i -> mServerResponse.handleError(i)));
//    }
//
//    private void handleResponseAvg(Response response) {
//        mTvClassAvg.setText("Rating:" + response.getMessage());
//    }


//    private void rateClass() {
//        String rate =  Integer.toString((int)simpleRatingBar.getRating());
//        String rating = "Rating :: " + simpleRatingBar.getRating();
//        Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
//        rateProcess(rate);
//
//    }
//
//    private void rateProcess(String rate) {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(pin,rate)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseRateProcess,i -> mServerResponse.handleError(i)));
//    }
//
//    private void handleResponseRateProcess(Response response) {
//        mServerResponse.showSnackBarMessage(response.getMessage());
//    }
//
    private void pullMessages(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessages(sid)
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
            messagesList.setAdapter(mAdapter);
        }
        else{
            mTvNoResults.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
//        mSocket.disconnect();
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        pullMessages();
    }

}
