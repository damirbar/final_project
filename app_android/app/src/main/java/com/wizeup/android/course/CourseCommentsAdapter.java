package com.wizeup.android.course;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.model.CourseMessage;
import com.squareup.picasso.Picasso;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseCommentsAdapter extends ArrayAdapter<CourseMessage> {
    private Context mContext;
    private List<CourseMessage> messagesList;


    public CourseCommentsAdapter(Activity context, ArrayList<CourseMessage> list) {
        super(context, 0, list);
        mContext = context;
        messagesList = list;
    }


    public List<CourseMessage> getMessagesList() {
        return messagesList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        CourseMessage currentMessage = messagesList.get(position);

        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_course_comment_item, parent, false);


        TextView msg = (TextView) listItem.findViewById(R.id.content);
        TextView mEmail = (TextView) listItem.findViewById(R.id.user_name);
        TextView mDate = (TextView) listItem.findViewById(R.id.creation_date);
        ImageView divView = (ImageView) listItem.findViewById(R.id.divider_img);
        ImageButton menu = listItem.findViewById(R.id.feed_item_menu);
        ImageView profileImage = (ImageView) listItem.findViewById(R.id.user_image);



        mEmail.setText(currentMessage.getName());
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

        String pic = currentMessage.getImage();
        if (pic != null && !(pic.isEmpty()))
            Picasso.with(mContext)
                    .load(pic)
                    .error(R.drawable.default_user_image)
                    .into(profileImage);



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

