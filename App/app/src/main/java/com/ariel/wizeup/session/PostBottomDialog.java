package com.ariel.wizeup.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ariel.wizeup.R;

public class PostBottomDialog extends BottomSheetDialogFragment {
    private String sid;
    private Button btn1;
    private Button btn2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_dialog_post, container, false);

        getData();
        initViews(v);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                dismiss();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuizActivity.class);
                intent.putExtra("sid", sid);
                startActivity(intent);
                dismiss();
            }
        });


        return v;
    }

    private void initViews(View v) {
        btn1 = (Button) v.findViewById(R.id.bottom_sheet_post_btn);
        btn2 = (Button) v.findViewById(R.id.bottom_sheet_quiz_btn);

    }


    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");

        }

    }
}