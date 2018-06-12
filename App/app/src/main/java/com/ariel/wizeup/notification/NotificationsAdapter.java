package com.ariel.wizeup.notification;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Notification;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<Notification> {
    private Context mContext;
    private List<Notification> notificationsList;


    public NotificationsAdapter(Activity context, ArrayList<Notification> list) {
        super(context, 0, list);
        mContext = context;
        notificationsList = list;
    }

    public List<Notification> getNotificationList() {
        return notificationsList;
    }

    public class MyNotificationHolder {
        TextView mNotificationType;
        TextView mNotificationDate;
        TextView mBody;

        public MyNotificationHolder(View v) {
            mNotificationType = v.findViewById(R.id.notification_type);
            mNotificationDate = v.findViewById(R.id.creation_date);
            mBody = v.findViewById(R.id.notification_body);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Notification currentNotification = notificationsList.get(position);

        MyNotificationHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.feed_notification_item, parent, false);
            holder = new MyNotificationHolder(view);
            view.setTag(holder);

        } else {
            holder = (MyNotificationHolder) view.getTag();

        }

        String type = currentNotification.getType().substring(0, 1).toUpperCase() + currentNotification.getType().substring(1);
        holder.mNotificationType.setText(type);

        String event = currentNotification.getBody().substring(0, 1).toUpperCase() + currentNotification.getBody().substring(1);
        holder.mBody.setText(event);

        //Date
        Date date = currentNotification.getDate();
        if (date != null) {
            Format formatter = new SimpleDateFormat("HH:mm EEE, d MMM yyyy");
            String s = formatter.format(date);
            holder.mNotificationDate.setText(s);
        }

        return view;
    }
}

