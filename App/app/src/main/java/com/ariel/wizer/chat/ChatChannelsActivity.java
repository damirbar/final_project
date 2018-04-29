package com.ariel.wizer.chat;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.ChatChannel;
import com.ariel.wizer.chat.ChatClient;
import com.ariel.wizer.utils.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class ChatChannelsActivity extends AppCompatActivity {
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;

    private ListView channelsList;
    private String mToken;

    private int currentChannel = 0;
    private List<ChatChannel> channels = new ArrayList<>();
    private ChatClient chatClient;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_channels);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        initViews();
        chatClient = ChatClient.getInstance();
        pullChannels();


    }

    private void pullChannels()
    {
        chatClient.getChannels(new ChatClient.ChatRestClientHandler() {
            @Override
            public void success(String jsonString) {
                Gson gson = new Gson();
                channels = Arrays.asList(gson.fromJson(jsonString, ChatChannel[].class));
                showSnackBarMessage("Channels pulled");
                invalidateOptionsMenu();
            }

            @Override
            public void fail(Throwable error, String frienlyErrorMsg) {
                showSnackBarMessage("Failed pulling channels");
            }
        });
    }

    private void setCurrentChannel(int channel, boolean reloadMessages){
        ChatChannel selected = channels.get(channel);
        setTitle(selected.getName());
        if (reloadMessages) {
            //pullMessages(channel);
        }
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (menu.size() == 0) {
            boolean gotChannels = false;

            for (int i=0;i<channels.size();i++) {
                ChatChannel channel = channels.get(i);
                menu.add(0,i,0, channel.getName());
                gotChannels = true;
            }

            if (gotChannels)
            {
                setCurrentChannel(0, true);
//                if (showChannelHint) {
//                    showChannelHint = false;
//                    showChannelChangeHint();
//                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setCurrentChannel(item.getItemId(), true);
        return true;
    }





    private void initSharedPreferences() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
    }


    private void initViews() {
        channelsList = (ListView) findViewById(R.id.chatChannelsList);
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_chat_channels),message, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
