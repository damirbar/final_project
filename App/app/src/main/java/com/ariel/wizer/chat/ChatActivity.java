package com.ariel.wizer.chat;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
import com.ariel.wizer.model.ChatMessage;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Validation;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.github.nkzawa.emitter.Emitter;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity  {
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private ListView messagesList;
    private TextView mTvNoResults;
    private TextView mchannelName;
    private ImageButton mMessageButton;
    private EditText mMessage;
    private ChatMessageAdapter mAdapter;
    private ChatMessage chatMessage;

    private String uid;
    private String name;

//    ChatMessage[] messages;



    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://socket-io-chat.now.sh/");
        } catch (URISyntaxException e) {}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity_main);


        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.mainLayout));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getUidAndName();
        pullMessages();

        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on("new message", onNewMessage);
        mSocket.connect();

        initViews();




    }



    private void initViews() {
        messagesList = (ListView) findViewById(R.id.chatMessagesList);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        mchannelName = (TextView) findViewById(R.id.channel_name);
        mMessageButton = (ImageButton) findViewById(R.id.sendChatMessageButton);
        mMessage = (EditText) findViewById(R.id.chatMessageEditText);
        mchannelName.setText(name);
        messagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mMessageButton.setOnClickListener(view -> attemptSend());

    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    try {
                        username = data.getString("username");
                        message = data.getString("message");

                        chatMessage = new  ChatMessage();
                        chatMessage.setContent(message);
                        chatMessage.setUserGuid("other");//remove
                        mAdapter.add(chatMessage);


                    } catch (JSONException e) {
                        return;
                    }

                    // add the message to view
                    mAdapter.add(chatMessage);
                    }
            });
        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String mUsername = name;
                    mSocket.emit("add user", mUsername);
                    mServerResponse.showSnackBarMessage(mUsername);
                }
            });
        }
    };

    private void attemptSend() {
        String message = mMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mMessage.setText("");
        mSocket.emit("new message", message);
        chatMessage = new  ChatMessage();
        chatMessage.setContent(message);
        chatMessage.setUserGuid(uid);//remove
        mAdapter.add(chatMessage);
    }



    private void getUidAndName() {
        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");
        name = intent.getExtras().getString("name");
    }


    private void pullMessages(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getChannelMessages(uid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(ChatMessage[] messages) {
        if(messages.length == 0){
            mTvNoResults.setVisibility(View.VISIBLE);
        }
        else{
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new ChatMessageAdapter(this, new ArrayList<>(Arrays.asList(messages)),uid);//add my uid????
            messagesList.setAdapter(mAdapter);
        }
    }

//    void sendMessage() {
//        String content =  mMessage.getText().toString();
//        if(Validation.validateFields(content) && content.length()<100){
//            message = new  ChatMessage();
//            message.setContent(content);
//            message.setChannelGuid(uid);
//            message.setUserGuid(uid);//remove
//            sendMessageProcess(message);
//        }
//    }
//
//    private void sendMessageProcess(ChatMessage message) {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishMessage(message)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseSend,this::handleError));
//    }
//
//    private void handleError(Throwable error) {
//        mServerResponse.handleError(error);
//    }
//
//
//    private void handleResponseSend(Response response) {
//        mMessage.setText(null);
//        mAdapter.add(message);
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
    }
}
