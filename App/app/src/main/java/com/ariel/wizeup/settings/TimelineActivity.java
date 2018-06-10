package com.ariel.wizeup.settings;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Event;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

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
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EventsAdapter mAdapter;
    private static int ADD_ITEMS = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.search_List));
        initViews();

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            loadEventsInit();
//            eventsList.setOnScrollListener(new EndlessScrollListener() {
//                @Override
//                public boolean onLoadMore(int page, int totalItemsCount) {
//                    // Triggered only when new data needs to be appended to the list
//                    // Add whatever code is needed to append new items to your AdapterView
////                mServerResponse.showSnackBarMessage("page: "+page+" totalItemsCount: "+totalItemsCount);
//                    loadEvents();
//                    // or loadNextDataFromApi(totalItemsCount);
//                    return true; // ONLY if more data is actually being loaded; false otherwise.
//                }
//            });

            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));


        loadEventsInit();

        eventsList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
//                mServerResponse.showSnackBarMessage("page: "+page+" totalItemsCount: "+totalItemsCount);
                loadEvents();
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

    }

    private EndlessScrollListener mEndlessScrollListener = new EndlessScrollListener() {

        @Override
        public boolean onLoadMore(int page, int totalItemsCount) {
            loadEvents();
            return false;
        }
    };

    private void initViews() {
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        eventsList = (ListView) findViewById(R.id.events);
        }


    private void loadEventsInit() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getEvents(0, ADD_ITEMS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseEventsInit, i -> mServerResponse.handleErrorDown(i)));

    }

    private void handleResponseEventsInit(Event event[]) {
        if (!(event.length == 0)) {
            ArrayList<Event> saveEvents = new ArrayList<>(Arrays.asList(event));
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new EventsAdapter(this, new ArrayList<>(saveEvents));
            eventsList.setAdapter(mAdapter);
        } else {
            mTvNoResults.setVisibility(View.VISIBLE);
        }

    }

    private void loadEvents() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getEvents(mAdapter.getEventsList().size(), ADD_ITEMS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseEvents, i -> mServerResponse.handleErrorDown(i)));

    }

    private void handleResponseEvents(Event event[]) {
        if (!(event.length == 0)) {
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
