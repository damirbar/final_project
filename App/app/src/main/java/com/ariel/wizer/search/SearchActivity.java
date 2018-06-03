package com.ariel.wizer.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Searchable;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Validation.validateFields;

public class SearchActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView searchList;
    private SearchView editSearch;
    private SearchListAdapter mAdapter;
    private TextView mTvNoResults;
    private TextView mTvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout_search));
        initViews();

        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }
            private void callSearch(String query) {
                if (validateFields(query.trim())) {
                    sendQuery(query);
                }
                else
                    searchList.setAdapter(null);
            }
        });

        searchList.setOnItemClickListener((parent, view1, position, id) -> {
            Object a = (Object) mAdapter.getItem(position);
            if(a instanceof User) {
                User user = (User) a;
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("id", user.getId_num());
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        searchList = (ListView) findViewById(R.id.search_List);
        editSearch = (SearchView) findViewById(R.id.searchView);
        mTvCancel = (TextView) findViewById(R.id.cancel_button);
        mTvCancel.setOnClickListener(view -> finish());
    }

    private void sendQuery(String query) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getSearch(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Searchable items) {
        if (!(items.getUsers().length == 0) || !(items.getSessions().length == 0) || !(items.getFiles().length == 0)) {
            ArrayList<Object> saveItems = new ArrayList<>();
            saveItems.addAll(Arrays.asList(items.getUsers()));
            saveItems.addAll(Arrays.asList(items.getSessions()));
            saveItems.addAll(Arrays.asList(items.getFiles()));
            saveItems.addAll(Arrays.asList(items.getCourses()));

            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SearchListAdapter(this, new ArrayList<>(saveItems));
            searchList.setAdapter(mAdapter);
        } else {
            mTvNoResults.setVisibility(View.VISIBLE);
            searchList.setAdapter(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
