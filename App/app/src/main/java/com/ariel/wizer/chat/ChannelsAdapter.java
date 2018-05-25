//package com.ariel.wizer.chat;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.TextView;
//
//import com.ariel.wizer.R;
//import com.ariel.wizer.model.ChatChannel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChannelsAdapter extends ArrayAdapter<ChatChannel> {
//    private Context mContext;
//    private List<ChatChannel> channelsList = new ArrayList<>();
//
//    public ChannelsAdapter( Context context, ArrayList<ChatChannel> list) {
//        super(context, 0 , list);
//        mContext = context;
//        channelsList = list;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//        View listItem = convertView;
//        if(listItem == null)
//            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item_channel,parent,false);
//
//        ChatChannel currentChannel = channelsList.get(position);
//
////        ImageView image = (ImageView)listItem.findViewById(R.id.imageView_poster);
////        image.setImageResource(currentChannel.getmImageDrawable());
//
//        TextView name = (TextView) listItem.findViewById(R.id.textView_name);
//        name.setText(currentChannel.getName());
//
//        return listItem;
//    }
//}
