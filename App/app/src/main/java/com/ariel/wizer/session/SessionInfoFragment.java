package com.ariel.wizer.session;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ariel.wizer.R;

public class SessionInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_info, container, false);
        initViews(view);


        return view;

    }

    private void initViews(View v) {

    }

}