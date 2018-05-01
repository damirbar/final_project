package com.ariel.wizer.ImageCrop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ariel.wizer.utils.Constants;

public class PicModeSelectDialogFragment extends DialogFragment {

    private String[] picMode = {PicModes.CAMERA, PicModes.GALLERY};

    private IPicModeSelectListener iPicModeSelectListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(picMode, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (iPicModeSelectListener != null)
                    iPicModeSelectListener.onPicModeSelected(picMode[which]);
            }
        });
        return builder.create();
    }

    public void setiPicModeSelectListener(IPicModeSelectListener iPicModeSelectListener) {
        this.iPicModeSelectListener = iPicModeSelectListener;
    }

    public interface IPicModeSelectListener {
        void onPicModeSelected(String mode);
    }
}
