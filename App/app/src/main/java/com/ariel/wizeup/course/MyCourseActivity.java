package com.ariel.wizeup.course;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.utils.Constants;

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
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoursesAdapter mAdapter;
    private FloatingActionButton mFB;
//    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout_my_classes));
        initViews();

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pullCourses();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));

        coursesList.setOnItemClickListener((parent, view1, position, id) -> {
            Course  C = mAdapter.getItem(position);
            Intent intent = new Intent(this,CourseActivity.class);
            intent.putExtra("cid", String.valueOf(C.getCid()));
            startActivity(intent);
        });


        coursesList.setOnScrollListener(new AbsListView.OnScrollListener(){
            private int mLastFirstVisibleItem;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    mFB.hide();
                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    mFB.show();
                }
                mLastFirstVisibleItem=firstVisibleItem;
            }
        });

    }

    private void initViews() {
//        layout =  findViewById(R.id.relative);
//        layout.setVisibility(View.GONE);
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        mTvNoResults = findViewById(R.id.tv_no_results);
        coursesList = findViewById(R.id.myClassesList);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mFB = findViewById(R.id.fb_add_corse);
        buttonBack.setOnClickListener(view -> finish());
        mFB.setOnClickListener(view -> addCourse());

    }

    private void addCourse() {
        Intent intent = new Intent(this,CreateCourseActivity.class);
        startActivity(intent);
    }

    private void pullCourses() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMyCourses()
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

//        layout.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        pullCourses();
    }



}
