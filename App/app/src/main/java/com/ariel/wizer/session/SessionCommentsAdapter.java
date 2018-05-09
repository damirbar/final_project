package com.ariel.wizer.session;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionCommentsAdapter extends ArrayAdapter<SessionMessage> {
    private Context mContext;
    private List<SessionMessage> messagesList;
    private boolean[] likeCheckBoxState;
    private boolean[] dislikeCheckBoxState;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;




    public SessionCommentsAdapter(Activity context, ArrayList<SessionMessage> list) {
        super(context, 0 , list);
        mContext = context;
        messagesList = list;
        likeCheckBoxState = new boolean[list.size()];
        dislikeCheckBoxState = new boolean[list.size()];
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(context);
    }

    public List<SessionMessage> getMessagesList() {
        return messagesList;
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


        final CheckBox likeCbx = (CheckBox) listItem.findViewById(R.id.like_cbx);
        final CheckBox dislikeCbx = (CheckBox) listItem.findViewById(R.id.dislike_cbx);
        likeCbx.setTag(position);
        likeCbx.setOnCheckedChangeListener(null);
        if(likeCheckBoxState[position])
            likeCbx.setChecked(true);
        else
            likeCbx.setChecked(false);
        likeCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer pos = (Integer)buttonView.getTag();
                if(isChecked){
                    if(dislikeCheckBoxState[pos]){
                        messagesList.get(position).setRating(messagesList.get(position).getRating()+2);
                        addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),+2);
                    }
                    else{
                        messagesList.get(position).setRating(messagesList.get(position).getRating()+1);
                        addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),+1);
                    }
                    likeCheckBoxState[pos]= true;
                    dislikeCheckBoxState[pos]= false;
                }
                else{
                    likeCheckBoxState[pos] = false;
                    messagesList.get(position).setRating(messagesList.get(position).getRating()-1);
                    addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),-1);
                }
                notifyDataSetChanged();
            }
        });
        dislikeCbx.setTag(position);
        dislikeCbx.setOnCheckedChangeListener(null);
        if(dislikeCheckBoxState[position])
            dislikeCbx.setChecked(true);
        else
            dislikeCbx.setChecked(false);
        dislikeCbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer pos = (Integer)buttonView.getTag();
                if(isChecked){
                    if(likeCheckBoxState[pos]){
                        messagesList.get(position).setRating(messagesList.get(position).getRating()-2);
                        addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),-2);
                    }
                    else{
                        messagesList.get(position).setRating(messagesList.get(position).getRating()-1);
                        addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),-1);
                    }
                    dislikeCheckBoxState[pos]= true;
                    likeCheckBoxState[pos] = false;

                }
                else{
                    dislikeCheckBoxState[pos] = false;
                    messagesList.get(position).setRating(messagesList.get(position).getRating()+1);
                    addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),+1);
                }
                notifyDataSetChanged();
            }
        });
        return listItem;
    }

    private void addRate(String sid, String msgid,int rate) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().rateMessage(sid,msgid,rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, ServerResponse::handleErrorRate));
    }

    private void handleResponse(Response response) { }

    @Override
    public void finalize() {
        mSubscriptions.unsubscribe();
    }


}