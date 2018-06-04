package com.ariel.wizeup.session;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.SessionMessage;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionPostsAdapter extends ArrayAdapter<SessionMessage> {
    private Context mContext;
    private List<SessionMessage> messagesList;
    private boolean[] likeCheckBoxState;
    private boolean[] dislikeCheckBoxState;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;


    public SessionPostsAdapter(Activity context, ArrayList<SessionMessage> list) {
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

        if (convertView == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_post_item, parent, false);

        TextView mLikeNum = (TextView) listItem.findViewById(R.id.like_num);
        TextView mDislikeNum = (TextView) listItem.findViewById(R.id.dislike_num);
        TextView mEmail = (TextView) listItem.findViewById(R.id.user_name);
        TextView msg = (TextView) listItem.findViewById(R.id.content);
        ImageView comView = (ImageView) listItem.findViewById(R.id.comment_btn);
        TextView comCount = (TextView) listItem.findViewById(R.id.comments_num);
        TextView mDate = (TextView) listItem.findViewById(R.id.creation_date);
        ImageButton menu = listItem.findViewById(R.id.feed_item_menu);


        //Date
        Date date = currentMessage.getDate();
        if(date!=null){
            Format formatter = new SimpleDateFormat("HH:mm EEE, d MMM yyyy");
            String s = formatter.format(date);
            mDate.setText(s);
        }

        comCount.setText(String.valueOf(currentMessage.getReplies().length));
        mEmail.setText(currentMessage.getEmail());
        msg.setText(currentMessage.getBody()[1]);
        mLikeNum.setText(String.valueOf(currentMessage.getLikes()));
        mDislikeNum.setText(String.valueOf(currentMessage.getDislikes()));

        comView.setOnClickListener(v -> {
            ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
        });


        final CheckBox likeCbx = (CheckBox) listItem.findViewById(R.id.like_cbx);
        final CheckBox dislikeCbx = (CheckBox) listItem.findViewById(R.id.dislike_cbx);


        likeCbx.setTag(position);
        likeCbx.setOnCheckedChangeListener(null);
        if(likeCheckBoxState[position])
            likeCbx.setChecked(true);
        else
            likeCbx.setChecked(false);

        likeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Integer pos = (Integer)buttonView.getTag();
            if(isChecked){
                if(dislikeCheckBoxState[pos]) {
                    messagesList.get(position).setDislikes(messagesList.get(position).getDislikes()-1);
                    dislikeCheckBoxState[pos]= false;
                }
                messagesList.get(position).setLikes(messagesList.get(position).getLikes() + 1);
                likeCheckBoxState[pos]= true;
            }
            else{
                likeCheckBoxState[pos] = false;
                messagesList.get(position).setLikes(messagesList.get(position).getLikes() - 1);
            }
            addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),1);
            notifyDataSetChanged();
        });

        dislikeCbx.setTag(position);
        dislikeCbx.setOnCheckedChangeListener(null);
        if(dislikeCheckBoxState[position])
            dislikeCbx.setChecked(true);
        else
            dislikeCbx.setChecked(false);
        dislikeCbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Integer pos = (Integer)buttonView.getTag();
            if(isChecked){
                if(likeCheckBoxState[pos]){
                    messagesList.get(position).setLikes(messagesList.get(position).getLikes() - 1);
                    likeCheckBoxState[pos] = false;
                }
                messagesList.get(position).setDislikes(messagesList.get(position).getDislikes() + 1);
                dislikeCheckBoxState[pos]= true;

            }
            else{
                dislikeCheckBoxState[pos] = false;
                messagesList.get(position).setDislikes(messagesList.get(position).getDislikes() - 1);
            }
            addRate(messagesList.get(position).getSid(),messagesList.get(position).get_id(),0);
            notifyDataSetChanged();
        });

        try {
            menu.setOnClickListener(v -> {
                switch (v.getId()) {
                    case R.id.feed_item_menu:
                        PopupMenu popup = new PopupMenu(mContext, v);
                        popup.getMenuInflater().inflate(R.menu.file_clipboard_popup,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    AlertDialogTheme();
                                    break;
                                case R.id.addtowishlist:
                                    Toast.makeText(mContext, "Delete File Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();
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

    private void addRate(String sid, String msgId,int rate) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().rateMessage(sid,msgId,rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, ServerResponse::handleErrorQuiet));
    }

    private void handleResponse(Response response) { }

    @Override
    public void finalize() {
        mSubscriptions.unsubscribe();
    }


    public void AlertDialogTheme() {
        AlertDialog.Builder AlertBuilder = new AlertDialog.Builder(
                mContext,R.style.Theme_Report_Dialog_Alert);
        AlertBuilder.setTitle("Report");
        AlertBuilder.setMessage("Would you like to report this post?");
        AlertBuilder.setCancelable(false);
        AlertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "You Have Selected YES.", Toast.LENGTH_LONG).show();
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
