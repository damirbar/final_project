package com.ariel.wizeup.settings;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Event;
import com.ariel.wizeup.model.SessionMessage;

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
        Event currentEvent = eventsList.get(position);

        MyEventHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.feed_event_item, parent, false);
            holder = new MyEventHolder(view);
            view.setTag(holder);

        } else {
            holder = (MyEventHolder)view.getTag();

        }

        String type = currentEvent.getType().substring(0, 1).toUpperCase() + currentEvent.getType().substring(1);
        holder.mEventType.setText(type);

        String event = currentEvent.getEvent().substring(0, 1).toUpperCase() + currentEvent.getEvent().substring(1);
        holder.mEvent.setText(event);

        //Date
        Date date = currentEvent.getDate();
        if (date != null) {
            Format formatter = new SimpleDateFormat("HH:mm EEE, d MMM yyyy");
            String s = formatter.format(date);
            holder.mEventDate.setText(s);
        }

        return view;
    }
}

