package com.ariel.wizer.session;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionMessagesAdapter extends ArrayAdapter<SessionMessage> {
    private Context mContext;
    private List<SessionMessage> messagesList;

    private SessionMessage currentMessage;


    public SessionMessagesAdapter(Context context, ArrayList<SessionMessage> list) {
        super(context, 0, list);
        mContext = context;
        messagesList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        currentMessage = messagesList.get(position);

        if (convertView == null) {

            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_post_item, parent, false);

        }

        TextView mlikeNum = (TextView) listItem.findViewById(R.id.like_num);
        TextView mDislikeNum = (TextView) listItem.findViewById(R.id.dislike_num);

        TextView mEmail = (TextView) listItem.findViewById(R.id.user_name);
        TextView msg = (TextView) listItem.findViewById(R.id.content);
        ImageView btnLike = (ImageView) listItem.findViewById(R.id.direct_btn_post_like);
        ImageView btnDisLike = (ImageView) listItem.findViewById(R.id.direct_btn_dislike);


        msg.setText(currentMessage.getBody()[1]);
        mlikeNum.setText(String.valueOf(currentMessage.getRating()));
//        mDislikeNum.setText(String.valueOf(currentMessage.getRating()));
        mEmail.setText(currentMessage.getEmail());

        btnDisLike.setOnClickListener(v -> {
            ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
        });

        btnLike.setOnClickListener(v -> {
            ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
        });

        return listItem;
    }




}
