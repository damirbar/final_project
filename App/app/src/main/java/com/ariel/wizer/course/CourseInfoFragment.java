package com.ariel.wizer.course;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ariel.wizer.R;

public class CourseInfoFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_info, container, false);
        initViews(view);
        return view;
    }
    private void initViews(View v) {
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

}