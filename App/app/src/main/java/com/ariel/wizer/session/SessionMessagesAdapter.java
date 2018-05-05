package com.ariel.wizer.session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.ariel.wizer.R;
import com.ariel.wizer.model.ChatChannel;
import com.ariel.wizer.model.SessionMessage;

public class SessionMessagesAdapter extends ArrayAdapter<SessionMessage> {
    private Context mContext;
    private List<SessionMessage> messagesList = new ArrayList<>();
    private final String question = "question";


    public SessionMessagesAdapter(Context context, ArrayList<SessionMessage> list) {
        super(context, 0 , list);
        mContext = context;
        messagesList = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        SessionMessage currentMessage = messagesList.get(position);

        if(currentMessage.getType().equalsIgnoreCase(question))
            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_post_item,parent,false);
        else{
            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_post_item,parent,false);

        }

        TextView msg1 = (TextView) listItem.findViewById(R.id.content);
        msg1.setText(currentMessage.getBody()[0]);
//        TextView msg2 = (TextView) listItem.findViewById(R.id.content);
//        msg2.setText(currentMessage.getBody()[1]);


        return listItem;
    }
}
