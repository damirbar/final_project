package com.wizeup.android.session;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.model.Response;
import com.wizeup.android.model.SessionMessage;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        super(context, 0, list);
        mContext = context;
        messagesList = list;
        likeCheckBoxState = new boolean[list.size()];
        dislikeCheckBoxState = new boolean[list.size()];
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(context);
    }

    public boolean[] getLikeCheckBoxState() {
        return likeCheckBoxState;
    }

    public void setLikeCheckBoxState(boolean[] likeCheckBoxState) {
        this.likeCheckBoxState = likeCheckBoxState;
    }

    public boolean[] getDislikeCheckBoxState() {
        return dislikeCheckBoxState;
    }

    public void setDislikeCheckBoxState(boolean[] dislikeCheckBoxState) {
        this.dislikeCheckBoxState = dislikeCheckBoxState;
    }

    public List<SessionMessage> getMessagesList() {
        return messagesList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        SessionMessage currentMessage = messagesList.get(position);

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_comment_item, parent, false);


        TextView msg = listItem.findViewById(R.id.content);
        TextView mDislikeNum = listItem.findViewById(R.id.dislike_num);
        TextView mLikeNum = listItem.findViewById(R.id.like_num);
        TextView mEmail = listItem.findViewById(R.id.user_name);
        TextView mDate = listItem.findViewById(R.id.creation_date);
        final CheckBox likeCbx = listItem.findViewById(R.id.like_cbx);
        final CheckBox dislikeCbx = listItem.findViewById(R.id.dislike_cbx);
        ImageView divView = listItem.findViewById(R.id.divider_img);
        ImageButton menu = listItem.findViewById(R.id.feed_item_menu);


        mEmail.setText(currentMessage.getNickname());
        mLikeNum.setText(String.valueOf(currentMessage.getLikes()));
        mDislikeNum.setText(String.valueOf(currentMessage.getDislikes()));
        msg.setText(currentMessage.getBody());


        if (position == 0) {
            divView.setVisibility(View.VISIBLE);
        } else
            divView.setVisibility(View.GONE);

        //Date
        Date date = currentMessage.getDate();
        if (date != null) {
            Format formatter = new SimpleDateFormat("HH:mm EEE, d MMM yyyy");
            String s = formatter.format(date);
            mDate.setText(s);
        }

        likeCbx.setTag(position);
        likeCbx.setOnCheckedChangeListener(null);
        if (likeCheckBoxState[position])
            likeCbx.setChecked(true);
        else
            likeCbx.setChecked(false);
        likeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Integer pos = (Integer) buttonView.getTag();
            if (isChecked) {
                if (dislikeCheckBoxState[pos]) {
                    messagesList.get(position).setDislikes(messagesList.get(position).getDislikes() - 1);
                    dislikeCheckBoxState[pos] = false;
                }
                messagesList.get(position).setLikes(messagesList.get(position).getLikes() + 1);
                likeCheckBoxState[pos] = true;
            } else {
                likeCheckBoxState[pos] = false;
                messagesList.get(position).setLikes(messagesList.get(position).getLikes() - 1);
            }
            addRate(messagesList.get(position).getSid(), messagesList.get(position).get_id(), 1);


            notifyDataSetChanged();
        });
        dislikeCbx.setTag(position);
        dislikeCbx.setOnCheckedChangeListener(null);
        if (dislikeCheckBoxState[position])
            dislikeCbx.setChecked(true);
        else
            dislikeCbx.setChecked(false);
        dislikeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Integer pos = (Integer) buttonView.getTag();
            if (isChecked) {
                if (likeCheckBoxState[pos]) {
                    messagesList.get(position).setLikes(messagesList.get(position).getLikes() - 1);
                    likeCheckBoxState[pos] = false;
                }
                messagesList.get(position).setDislikes(messagesList.get(position).getDislikes() + 1);
                dislikeCheckBoxState[pos] = true;

            } else {
                dislikeCheckBoxState[pos] = false;
                messagesList.get(position).setDislikes(messagesList.get(position).getDislikes() - 1);
            }
            addRate(messagesList.get(position).getSid(), messagesList.get(position).get_id(), 0);
            notifyDataSetChanged();
        });

        try {
            menu.setOnClickListener(v -> {
                switch (v.getId()) {
                    case R.id.feed_item_menu:
                        PopupMenu popup = new PopupMenu(mContext, v);
                        popup.getMenuInflater().inflate(R.menu.post_popup,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    AlertDialogTheme();
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        });
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listItem;
    }


    private void addRate(String sid, String msgId, int rate) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().rateMessage(sid, msgId, rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, ServerResponse::handleErrorQuiet));
    }


    private void handleResponse(Response response) {
    }

    @Override
    public void finalize() {
        mSubscriptions.unsubscribe();
    }

    public void AlertDialogTheme() {
        AlertDialog.Builder AlertBuilder = new AlertDialog.Builder(
                mContext, R.style.Theme_Report_Dialog_Alert);
        AlertBuilder.setTitle("Report");
        AlertBuilder.setMessage("Would you like to report this post?");
        AlertBuilder.setCancelable(false);
        AlertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertBuilder.setNegativeButton("N0", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = AlertBuilder.create();
        dialog.show();
    }


}
