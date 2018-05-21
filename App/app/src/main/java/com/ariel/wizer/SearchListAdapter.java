package com.ariel.wizer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ariel.wizer.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> searchList;

    public SearchListAdapter(Activity context, ArrayList<User> list) {
        super(context, 0, list);
        mContext = context;
        searchList = list;
    }

    public List<User> getSearchList() {
        return searchList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        User currentUser = searchList.get(position);

        if (convertView == null) {

            listItem = LayoutInflater.from(mContext).inflate(R.layout.row_search_item, parent, false);

        }
        TextView mSearchName = (TextView) listItem.findViewById(R.id.stringName);

        mSearchName.setText(currentUser.getFname());


        return listItem;
    }
}

