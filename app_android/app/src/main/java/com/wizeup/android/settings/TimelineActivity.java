package com.wizeup.android.settings;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.wizeup.android.R;
import com.wizeup.android.model.Event;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class TimelineActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView eventsList;
    private TextView mTvNoResults;
    private PullRefreshLayout mSwipeRefreshLayout;
    private EventsAdapter mAdapter;
    private static int ADD_ITEMS = 20;
    private boolean first = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.events));
        mAdapter = new EventsAdapter(this, new ArrayList<>());

        initViews();
        loadEvents();

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            first = true;
            mAdapter = new EventsAdapter(this, new ArrayList<>());
            loadEvents();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));


        eventsList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadEvents();
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    private void initViews() {
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mTvNoResults = findViewById(R.id.tv_no_results);
        eventsList = findViewById(R.id.events);
        buttonBack.setOnClickListener(view -> finish());
    }

    private void loadEvents() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getEvents(mAdapter.getEventsList().size(), ADD_ITEMS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseEvents, i -> mServerResponse.handleErrorDown(i)));

    }

    private void handleResponseEvents(Event event[]) {
        if (first) {
            if (event.length != 0) {
                ArrayList<Event> saveEvents = new ArrayList<>(Arrays.asList(event));
                mTvNoResults.setVisibility(View.GONE);
                mAdapter = new EventsAdapter(this, new ArrayList<>(saveEvents));
                eventsList.setAdapter(mAdapter);
            } else {
                mTvNoResults.setVisibility(View.VISIBLE);
            }
            first = false;
        } else if (event.length != 0) {
            ArrayList<Event> saveEvents = new ArrayList<>(Arrays.asList(event));
            mAdapter.getEventsList().addAll(saveEvents);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
