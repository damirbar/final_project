//package com.ariel.wizer.chat;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.ariel.wizer.R;
//import com.ariel.wizer.model.ChatMessage;
//
//public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
//    private Context mContext;
//    private String userGuid;
//    private List<ChatMessage> messagesList = new ArrayList<>();
//
//    public ChatMessageAdapter( Context context, ArrayList<ChatMessage> list, String _userGuid) {
//        super(context, 0 , list);
//        mContext = context;
//        messagesList = list;
//        userGuid = _userGuid;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
//        ChatMessage currentMessage = messagesList.get(position);
//        View listItem = convertView;
//        if(currentMessage.getUserGuid().equalsIgnoreCase(userGuid))
//            listItem = LayoutInflater.from(mContext).inflate(R.layout.chat_row_right,parent,false);
//        else{
//            listItem = LayoutInflater.from(mContext).inflate(R.layout.chat_row_left,parent,false);
//            //          ImageView image = (ImageView)listItem.findViewById(R.id.avatarImageView);
////          image.setImageResource();
//
//        }
//        TextView message = (TextView) listItem.findViewById(R.id.chatContentTextView);
//        message.setText(currentMessage.getContent());
//        return listItem;
//    }
//}
