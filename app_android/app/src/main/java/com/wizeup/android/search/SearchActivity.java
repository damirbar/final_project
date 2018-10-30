package com.wizeup.android.search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;

import com.wizeup.android.R;
import com.wizeup.android.model.Searchable;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Validation.validateFields;

public class SearchActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private SearchView editSearch;
    private TabLayout tabLayout;
    private SearchPagerAdapter adapter;
    private String saveQuery;

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
                saveQuery = query;

                if (validateFields(query.trim())) {
                    sendQuery(query);
                }
                else{
                    adapter.setSearchItems(new Searchable());
                    adapter.notifyDataSetChanged();

                }
            }
        });

        final ViewPager viewPager = findViewById(R.id.pager);
        adapter = new SearchPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
    }

    private void initViews() {
        editSearch = findViewById(R.id.searchView);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> finish());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Courses"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));
        tabLayout.addTab(tabLayout.newTab().setText("Sessions"));
        tabLayout.addTab(tabLayout.newTab().setText("Users"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    }

    private void sendQuery(String query) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getSearch(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Searchable items) {
        if(!saveQuery.isEmpty()){
            adapter.setSearchItems(items);
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
