package com.ariel.wizer.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.ChatChannel;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ChatChannelsActivity extends AppCompatActivity {
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private ListView channelsList;
    private TextView mTvNoResults;
    private ChatChannel[] saveChannels;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_channels);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_chat_channels));
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        pullChannels();
        channelsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(),ChatActivity.class);
                String uid = saveChannels[position].getGuid();
                String name = saveChannels[position].getName();
                intent.putExtra("uid",uid);
                intent.putExtra("name",name);
                startActivity(intent);
                }
        });
    }

    private void initViews() {
        channelsList = (ListView) findViewById(R.id.chatChannelsList);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);

    }

    private void pullChannels(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getChannels()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(ChatChannel[] channels) {
        if(channels.length == 0){
            mTvNoResults.setVisibility(View.VISIBLE);
        }
        else{
            saveChannels = channels;
            mTvNoResults.setVisibility(View.GONE);
            ChannelsAdapter mAdapter = new ChannelsAdapter(this, new ArrayList<>(Arrays.asList(channels)));
            channelsList.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
