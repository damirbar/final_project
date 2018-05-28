package com.ariel.wizer.course;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.CourseFile;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.session.SessionPostsAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CourseFilesFragment extends Fragment {

    private CompositeSubscription mSubscriptions;
    private ListView filesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFBAddFile;

    private CourseFilesAdapter mAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_files, container, false);
        initViews(view);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.activity_files_feed));
        pullFiles();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullFiles();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        return view;
    }

    private void initViews(View v) {
        mTvNoResults = (TextView) v.findViewById(R.id.tv_no_results);
        filesList = (ListView) v.findViewById(R.id.files_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mFBAddFile = (FloatingActionButton) v.findViewById(R.id.fb_add_file);
        mFBAddFile.setOnClickListener(view -> addFile());
    }

    private void addFile(){
    }

    private void pullFiles(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getAllFiles()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(CourseFile courseFiles[]) {
        if(!(courseFiles.length == 0)){
            ArrayList<CourseFile> saveFiles = new ArrayList<>(Arrays.asList(courseFiles));
            Collections.reverse(saveFiles);
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new CourseFilesAdapter(this.getActivity(), new ArrayList<>(saveFiles));
            filesList.setAdapter(mAdapter);
        }
        else{
            mTvNoResults.setVisibility(View.VISIBLE);

        }
    }



    @Override
    public void onResume()
    {
        super.onResume();
        pullFiles();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}