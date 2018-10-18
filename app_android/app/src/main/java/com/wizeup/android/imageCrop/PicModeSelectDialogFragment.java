package com.wizeup.android.imageCrop;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wizeup.android.R;

public class PicModeSelectDialogFragment extends BottomSheetDialogFragment {

    public static final String TAG = PicModeSelectDialogFragment.class.getSimpleName();

    private Button btn1;
    private Button btn2;
    IPicModeSelectListener iPicModeSelectListener;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_pic_dialog, container, false);

        initViews(v);

        btn1.setOnClickListener(v1 -> {
            iPicModeSelectListener.onPicModeSelected(PicModes.CAMERA);
            dismiss();
        });
        btn2.setOnClickListener(v12 -> {
            iPicModeSelectListener.onPicModeSelected(PicModes.GALLERY);
            dismiss();
        });


        return v;
    }


    private void initViews(View v) {
        btn1 = v.findViewById(R.id.bottom_sheet_cam_btn);
        btn2 = v.findViewById(R.id.bottom_sheet_gallery_btn);

    }


    public interface IPicModeSelectListener {
        void onPicModeSelected(String mode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            iPicModeSelectListener = (PicModeSelectDialogFragment.IPicModeSelectListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

}
