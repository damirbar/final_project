package com.ariel.wizer.demo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FileUploadActivity extends AppCompatActivity {

    public static final String TAG = FileUploadActivity.class.getSimpleName();

    private static final int INTENT_REQUEST_CODE = 100;

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private Button mBtImageSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.la));
        initViews();
    }

    private void initViews() {

        mBtImageSelect = findViewById(R.id.btn_select_image);

        mBtImageSelect.setOnClickListener((View view) -> {


            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/mp4*");

            try {
                startActivityForResult(intent, INTENT_REQUEST_CODE);

            } catch (ActivityNotFoundException e) {

                e.printStackTrace();
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == INTENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                try {

                    InputStream is = getContentResolver().openInputStream(data.getData());

                    uploadFile(RetrofitRequests.getBytes(is));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void uploadFile(byte[] bytes) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("video/mp4"), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("recfile", "video.mp4", requestFile);
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().uploadFile(body,"1")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));
    }


    private void handleResponse(Response response) {
        mServerResponse.showSnackBarMessage("fix");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}