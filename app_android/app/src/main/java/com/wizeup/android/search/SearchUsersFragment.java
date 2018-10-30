package com.wizeup.android.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wizeup.android.R;
import com.wizeup.android.model.User;
import com.wizeup.android.profile.ProfileActivity;

import java.util.ArrayList;

public class SearchUsersFragment extends Fragment {

    private ListView searchList;
    private RelativeLayout results;
    private SearchListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_feed, container, false);
        initViews(view);
        getData();

        searchList.setOnItemClickListener((parent, view1, position, id) -> {
            Object a = (Object) mAdapter.getItem(position);
            if (a instanceof User) {
                User user = (User) a;
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("id", user.getId_num());
                startActivity(intent);
            }
        });

        return view;
    }

    private void initViews(View v) {
        results = v.findViewById(R.id.result);
        searchList = v.findViewById(R.id.search_List);
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ArrayList<User> users = bundle.getParcelableArrayList("users");

            if (users != null) {
                if (users.size() != 0) {
                    mAdapter = new SearchListAdapter(getActivity(), new ArrayList<>(users));
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

