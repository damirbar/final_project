package com.ariel.wizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.chat.ChatMessageAdapter;
import com.ariel.wizer.model.ChatMessage;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SessionActivity extends AppCompatActivity {

    private CompositeSubscription mSubscriptions;
    private Button askButton;
    private RatingBar simpleRatingBar;
    private TextView mTvClassAvg;
    private TextView mTvClassPin;
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private LinearLayout mtaskBar;
    private ImageButton mMessageButton;
    private EditText mMessage;
    private final String question = "question";




    private String pin;
    private final int delay = 5000; //milliseconds

    private SessionMessagesAdapter mAdapter;
    private SessionMessage sessionMessage;

//    private Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket(Constants.BASE_URL);
//        } catch (URISyntaxException e) {}
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
//        mSocket.on(Socket.EVENT_CONNECT,onConnect);
//        mSocket.connect();

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_session));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getPin();
        initViews();
        pullMessages();
//        classAvgProcess();

//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                classAvgProcess();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
    }

    private void initViews() {
        mTvClassPin = (TextView) findViewById(R.id.tVclassPin);
        mTvClassPin.setText("Class Pin:" + pin);
        mTvClassAvg = (TextView) findViewById(R.id.tVclassAvg);
        simpleRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mtaskBar = (LinearLayout) findViewById(R.id.taskBar);
        mMessageButton = (ImageButton) findViewById(R.id.sendChatMessageButton);
        askButton = (Button) findViewById(R.id.ask_Button);
        mTvNoResults = (TextView) findViewById(R.id.tv_no_results);
        messagesList = (ListView) findViewById(R.id.sessionMessagesList);
        mMessage = (EditText) findViewById(R.id.chatMessageEditText);

        askButton.setOnClickListener(view -> openQuestion());
    }

//    private Emitter.Listener onConnect = new Emitter.Listener() {
//        @Override
//        public void call(Object... args) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    String mUsername = mRetrofitRequests.getmToken();
////                    mSocket.emit("add user", mUsername);
//                }
//            });
//        }
//    };

    private void openQuestion(){
        mTvNoResults.setVisibility(View.VISIBLE);
        mMessageButton.setOnClickListener(view -> attemptSend());
    }

    private void attemptSend() {
        String strMessage = mMessage.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }

        mMessage.setText("");
        SessionMessage  message = new  SessionMessage();
        message.setSid(pin);
        message.setType(question);
        String Body[]={"",strMessage};
        message.setBody(Body);
        sendMessage(message);

    }

        private void sendMessage(SessionMessage  message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishSessionMessage(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendMessage,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseSendMessage(Response response) {
        mTvNoResults.setVisibility(View.GONE);
        pullMessages();
    }





    private void getPin() {
        Intent intent = getIntent();
        pin = intent.getExtras().getString("pin");
    }

//    private void classAvgProcess() {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getStudentsCount(pin)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseAvg,i -> mServerResponse.handleError(i)));
//    }
//
//    private void handleResponseAvg(Response response) {
//        mTvClassAvg.setText("Rating:" + response.getMessage());
//    }


//    private void rateClass() {
//        String rate =  Integer.toString((int)simpleRatingBar.getRating());
//        String rating = "Rating :: " + simpleRatingBar.getRating();
//        Toast.makeText(getApplicationContext(), rating, Toast.LENGTH_LONG).show();
//        rateProcess(rate);
//
//    }
//
//    private void rateProcess(String rate) {
//        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().changeVal(pin,rate)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(this::handleResponseRateProcess,i -> mServerResponse.handleError(i)));
//    }
//
//    private void handleResponseRateProcess(Response response) {
//        mServerResponse.showSnackBarMessage(response.getMessage());
//    }
//








    private void pullMessages(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessages(pin)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(Session session) {
        if(session.getMessages().length == 0){
            mTvNoResults.setVisibility(View.VISIBLE);
        }
        else{
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SessionMessagesAdapter(this, new ArrayList<>(Arrays.asList(session.getMessages())));
            messagesList.setAdapter(mAdapter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
//        mSocket.disconnect();
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);

    }
}
