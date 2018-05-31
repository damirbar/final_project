package com.ariel.wizer.search;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.CourseFile;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private List<Object> searchList;

    public SearchListAdapter(Activity context, ArrayList<Object> list) {
        super(context, 0, list);
        mContext = context;
        searchList = list;
    }

    public List<Object> getSearchList() {
        return searchList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

//        if (convertView == null) {
//
//            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_user, parent, false);
//        }


        Object a = (Object) getItem(position);

        if(a instanceof User) {
            User user = (User) a;

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_user, parent, false);
            TextView mName = (TextView) listItem.findViewById(R.id.user_name);
            ImageView profileImage = (ImageView) listItem.findViewById(R.id.user_image);;


            String disName = user.getDisplay_name();
            if(disName!=null && !(disName.isEmpty())){
                mName.setText(user.getDisplay_name());
            }
            else{
                mName.setText(user.getFname()+" " + user.getLname());
            }

            String pic = user.getProfile_img();
            if(pic!=null&&!(pic.isEmpty()))
                Picasso.get().load(pic).into(profileImage);

        }

        if(a instanceof Session) {
            Session session = (Session) a;

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_session, parent, false);
            TextView mName = (TextView) listItem.findViewById(R.id.sid_name);
            TextView mDate = (TextView) listItem.findViewById(R.id.creation_date);
            mName.setText(session.getName());
//            mDate.setText(session.getCreation_date());
        }

        if(a instanceof CourseFile) {
            CourseFile file = (CourseFile) a;

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_file, parent, false);
            TextView mName = (TextView) listItem.findViewById(R.id.file_name);
            TextView mDate = (TextView) listItem.findViewById(R.id.creation_date);

            mName.setText(file.getName());
//            mDate.setText(file.getCreation_date());
        }

        return listItem;
    }
}

