package com.ariel.wizeup.course;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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

import com.ariel.wizeup.R;
import com.ariel.wizeup.dialogs.UploadingDialog;
import com.ariel.wizeup.model.CourseFile;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.session.SessionCommentActivity;
import com.ariel.wizeup.session.UploadCourseFilesAdapter;

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
import static com.ariel.wizeup.file.OpenFile.getFileDetailFromUri;
import static com.ariel.wizeup.network.RetrofitRequests.getBytes;

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
    private View alert;
    private ArrayList<CourseFile> uploadFiles;
    private UploadCourseFilesAdapter mAdapterUpload;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_files, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        getData();
        initViews(view);
        alert = view.findViewById(R.id.activity_files_feed);
        uploadFiles = new ArrayList<>();
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(alert);

        filesList.setOnItemClickListener((parent, view1, position, id) -> {
            long viewId = view1.getId();
            if (viewId == R.id.cancel) {
                cancelUpload();
            }
        });


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
        mTvNoResults = v.findViewById(R.id.tv_no_results);
        filesList = v.findViewById(R.id.files_list);
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mFBAddFile = v.findViewById(R.id.fb_add_file);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mFBAddFile.setOnClickListener(view -> addFile());
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cid = bundle.getString("cid");
        }
    }

    private void addFile() {
        try {
            String[] mimeTypes =
                    {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                            "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                            "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                            "text/plain",
                            "application/pdf",
                            "application/zip"};

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), INTENT_REQUEST_CODE);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {

                    Uri uri = data.getData();
                    String fileName = getFileDetailFromUri(getContext(), uri);


//                    mTvNoResults.setVisibility(View.GONE);
//                    CourseFile f = new CourseFile();
//                    f.setName(fileName);
//                    uploadFiles.add(0,f);
//                    mAdapterUpload = new UploadCourseFilesAdapter(this.getActivity(), new ArrayList<>(uploadFiles));
//                    filesList.setAdapter(mAdapterUpload);


                    InputStream is = getActivity().getContentResolver().openInputStream(data.getData());
                    tryUploadFile(getBytes(is), fileName);
                    is.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    mServerResponse.showSnackBarMessage("File didn't upload.");
                }
            }
        }
    }


    private void tryUploadFile(byte[] bytes, String fileName) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("recfile", fileName, requestFile);
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().uploadFile(cid,body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUploadFile, this::handleError));
    }


    private void handleResponseUploadFile(Response response) {
        pullFiles();
    }

    private void handleError(Throwable error) {
        mServerResponse.handleError(error);
        pullFiles();
    }


    private void pullFiles() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getCourseFiles(cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull, i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(CourseFile files[]) {
        if (!(files.length == 0)) {
            ArrayList<CourseFile> saveFiles = new ArrayList<>(Arrays.asList(files));
            Collections.reverse(saveFiles);
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new CourseFilesAdapter(this.getActivity(), new ArrayList<>(saveFiles), alert);
            filesList.setAdapter(mAdapter);
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

    public void cancelUpload() {
        mSubscriptions.unsubscribe();
        mSubscriptions = new CompositeSubscription();
    }


}