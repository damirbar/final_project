package com.ariel.wizer.course;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Course;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MyCourseActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;

    private ListView coursesList;
    private ImageButton buttonBack;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoursesAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout_my_classes));

        initViews();
        pullClasses();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullClasses();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        coursesList.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(this,CourseTabActivity.class);
            startActivity(intent);
        });

    }

    private void initViews() {
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        coursesList = (ListView) findViewById(R.id.myClassesList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        buttonBack.setOnClickListener(view -> finish());
    }

    private void pullClasses() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getAllCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(Course courses[]) {
        if (!(courses.length == 0)) {
            ArrayList<Course> saveCourses = new ArrayList<>(Arrays.asList(courses));
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new CoursesAdapter(this, new ArrayList<>(saveCourses));
            coursesList.setAdapter(mAdapter);
        } else {
            mTvNoResults.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        pullClasses();
    }



}
