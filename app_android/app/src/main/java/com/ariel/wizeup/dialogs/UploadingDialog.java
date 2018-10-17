package com.ariel.wizeup.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ariel.wizeup.R;

public class UploadingDialog extends BottomSheetDialogFragment {

    public interface OnCallbackCancel {
        public void cancelUpload();
    }

    public static final String TAG = UploadingDialog.class.getSimpleName();

    private ImageButton mCancel;
    UploadingDialog.OnCallbackCancel mCallback;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_uploading_dialog, container, false);
        initViews(v);

        return v;
    }

    private void initViews(View v) {
        mCancel = v.findViewById(R.id.cancel);
        mCancel.setOnClickListener(view -> tryCancelUpload());
    }

    private void tryCancelUpload() {
        mCallback.cancelUpload();
        dismiss();

    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (UploadingDialog.OnCallbackCancel) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }



}