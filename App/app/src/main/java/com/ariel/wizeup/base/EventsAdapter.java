package com.ariel.wizeup.base;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Event;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsAdapter extends ArrayAdapter<Event> {
    private Context mContext;
    private List<Event> eventsList;


    public EventsAdapter(Activity context, ArrayList<Event> list) {
        super(context, 0, list);
        mContext = context;
        eventsList = list;
    }

    public List<Event> getEventsList() {
        return eventsList;
    }

    public class MyEventHolder {
        TextView mEventType;
        TextView mEventDate;
        TextView mEvent;

        public MyEventHolder(View v) {
            mEventType = v.findViewById(R.id.event_type);
            mEventDate = v.findViewById(R.id.creation_date);
            mEvent = v.findViewById(R.id.event_content);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyEventHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.feed_event_item, parent, false);
            holder = new MyEventHolder(view);
            view.setTag(holder);

        } else {
            holder = (MyEventHolder)view.getTag();

        }

        String type = eventsList.get(position).getType().toUpperCase();
        holder.mEventType.setText(type);
        holder.mEvent.setText(eventsList.get(position).getEvent());

        //Date
        Date date = eventsList.get(position).getDate();
        if (date != null) {
            Format formatter = new SimpleDateFormat("HH:mm EEE, d MMM yyyy");
            String s = formatter.format(date);
            holder.mEventDate.setText(s);
        }

        return view;
    }
}

