package com.wizeup.android.course;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.model.Session;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionsAdapter extends ArrayAdapter<Session> {
    private Context mContext;
    private List<Session> sessionsList;


    public SessionsAdapter(Activity context, ArrayList<Session> list) {
        super(context, 0, list);
        mContext = context;
        sessionsList = list;
    }


    public class MyHolder {
        TextView mName;
        TextView mDate;

        public MyHolder(View v) {
            mName = (TextView) v.findViewById(R.id.sid_name);
            mDate = (TextView) v.findViewById(R.id.creation_date);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.search_item_session, parent, false);
            holder = new MyHolder(view);
            view.setTag(holder);

        } else {
            holder = (MyHolder) view.getTag();

        }

        holder.mName.setText(sessionsList.get(position).getName());
        //Date
        Date date = sessionsList.get(position).getCreation_date();
        if (date != null) {
            Format formatter = new SimpleDateFormat("dd/MM/yyyy");
            String s = formatter.format(date);
            holder.mDate.setText(s);
        }

        return view;
    }

}

