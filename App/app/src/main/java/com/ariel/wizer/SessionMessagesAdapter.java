package com.ariel.wizer;

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

    public SessionMessagesAdapter(Context context, ArrayList<SessionMessage> list) {
        super(context, 0 , list);
        mContext = context;
        messagesList = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_session_msg_q,parent,false);

        SessionMessage currentMessage = messagesList.get(position);

        TextView msg = (TextView) listItem.findViewById(R.id.textView_msg);
        msg.setText(currentMessage.getContent());

        return listItem;
    }
}
