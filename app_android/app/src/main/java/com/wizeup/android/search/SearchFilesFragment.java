package com.wizeup.android.search;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wizeup.android.R;
import com.wizeup.android.model.CourseFile;

import java.util.ArrayList;

public class SearchFilesFragment extends Fragment {

    private ListView searchList;
    private RelativeLayout results;
    private SearchListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_feed, container, false);
        initViews(view);
        getData();

        return view;
    }

    private void initViews(View v) {
        results = v.findViewById(R.id.result);
        searchList = v.findViewById(R.id.search_List);
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ArrayList<CourseFile> files = bundle.getParcelableArrayList("files");

            if (files != null) {
                if (files.size() != 0) {
                    mAdapter = new SearchListAdapter(getActivity(), new ArrayList<>(files));
                    searchList.setAdapter(mAdapter);
                } else {
                    results.setVisibility(View.VISIBLE);
                    searchList.setAdapter(null);
                }
            }
            else{
                results.setVisibility(View.GONE);
                searchList.setAdapter(null);
            }
        }
    }


}

