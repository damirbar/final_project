package com.ariel.wizer.course;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.ariel.wizer.R;
import com.ariel.wizer.model.CourseFile;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static com.ariel.wizer.network.RetrofitRequests.getBytes;

public class CourseFilesFragment extends Fragment {

    private CompositeSubscription mSubscriptions;
    private ListView filesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFBAddFile;
    private CourseFilesAdapter mAdapter;
    private String cid;
    private static final int INTENT_REQUEST_CODE = 100;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_files, container, false);
        getData();
        initViews(view);

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.activity_files_feed));

        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            pullFiles();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 1000));

        filesList.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    mFBAddFile.hide();
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    mFBAddFile.show();
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });
        pullFiles();
        return view;
    }

    private void initViews(View v) {
        mTvNoResults = (TextView) v.findViewById(R.id.tv_no_results);
        filesList = (ListView) v.findViewById(R.id.files_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mFBAddFile = (FloatingActionButton) v.findViewById(R.id.fb_add_file);
        mFBAddFile.setOnClickListener(view -> addFile());
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cid = bundle.getString("cid");
        }
    }

    private void addFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, INTENT_REQUEST_CODE);

        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri= data.getData();
                    File file= new File(uri.getPath());
                    InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                    tryUploadFile(getBytes(is),file.getName().trim());
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void tryUploadFile(byte[] bytes,String fileName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("recfile", fileName, requestFile);
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().uploadFile(body, cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUploadFile, i -> mServerResponse.handleError(i)));
    }


    private void handleResponseUploadFile(Response response) {
        mServerResponse.showSnackBarMessage(response.getMessage());
    }


    private void pullFiles() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getAllFilesById(cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(CourseFile courseFiles[]) {
        if (!(courseFiles.length == 0)) {
            ArrayList<CourseFile> saveFiles = new ArrayList<>(Arrays.asList(courseFiles));
            Collections.reverse(saveFiles);
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new CourseFilesAdapter(this.getActivity(), new ArrayList<>(saveFiles));
            filesList.setAdapter(mAdapter);
        } else {
            mTvNoResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}