package com.ariel.wizer.session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.SessionMessage;

import java.util.ArrayList;
import java.util.List;

public class SessionCommentsAdapter extends ArrayAdapter<SessionMessage> {
    private Context mContext;
    private List<SessionMessage> messagesList = new ArrayList<>();


    public SessionCommentsAdapter(Context context, ArrayList<SessionMessage> list) {
        super(context, 0 , list);
        mContext = context;
        messagesList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        SessionMessage currentMessage = messagesList.get(position);

        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_comment_item,parent,false);


        TextView msg = (TextView) listItem.findViewById(R.id.content);
        msg.setText(currentMessage.getBody()[1]);

        TextView mDislikeNum = (TextView) listItem.findViewById(R.id.dislike_num);
//        mDislikeNum.setText(String.valueOf(currentMessage.getRating()));


        TextView mlikeNum = (TextView) listItem.findViewById(R.id.like_num);
        mlikeNum.setText(String.valueOf(currentMessage.getRating()));

        TextView mEmail = (TextView) listItem.findViewById(R.id.user_name);
        mEmail.setText(currentMessage.getEmail());

        ImageView btnLike = (ImageView) listItem.findViewById(R.id.direct_btn_like);
        ImageView btnDisLike = (ImageView) listItem.findViewById(R.id.direct_btn_dislike);

        btnDisLike.setOnClickListener(v -> {
            ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
        });

        btnLike.setOnClickListener(v -> {
            ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
        });


        return listItem;
    }
}
