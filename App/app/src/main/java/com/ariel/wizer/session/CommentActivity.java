package com.ariel.wizer.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.Session;
import com.ariel.wizer.model.SessionMessage;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;

import java.util.ArrayList;
import java.util.Arrays;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CommentActivity extends AppCompatActivity {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private ListView commentsList;

    private EditText mComtText;
    private ImageButton buttonBack;
    private TextView buttonSend;
    private final String answer = "answer";
    private String sid;
    private SessionMessage[] saveComments;

//    private String guid;

    private SessionCommentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_comment));
        sid = getIntent().getStringExtra("sid");

//        SessionMessage msg = (SessionMessage) getIntent().getSerializableExtra("msg");


//        guid = getIntent().getStringExtra("guid");
        initViews();
        pullComments();


        commentsList.setOnItemClickListener((parent, view1, position, id) -> {
            long viewId = view1.getId();
            String guid = saveComments[position].get_id();
            if (viewId == R.id.direct_btn_like) {
                Toast.makeText(this, "Like", Toast.LENGTH_SHORT).show();
                addRate(guid,1);
                saveComments[position].setRating(saveComments[position].getRating()+1);
                mAdapter.notifyDataSetChanged();

            }
            else if(viewId == R.id.direct_btn_dislike) {
                Toast.makeText(this, "DisLike", Toast.LENGTH_SHORT).show();
                addRate(guid,-1);
                saveComments[position].setRating(saveComments[position].getRating()-1);///change to +1
                mAdapter.notifyDataSetChanged();
            }
        });


    }

    private void initViews() {
        commentsList = (ListView) findViewById(R.id.comments);
        mComtText = (EditText) findViewById(R.id.com_text);
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonSend = (TextView) findViewById(R.id.send_btn);
        buttonSend.setOnClickListener(view -> attemptSendCom());
        buttonBack.setOnClickListener(view -> goBack());
    }


    private void pullComments(){
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessages(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(Session session) {
        if(session.getMessages().length == 0){
        }
        else{
            saveComments = session.getMessages();
            mAdapter = new SessionCommentsAdapter(this, new ArrayList<>(Arrays.asList(session.getMessages())));
            commentsList.setAdapter(mAdapter);
        }
    }


    private void goBack() {
        finish();
    }

    private void attemptSendCom() {
        String strMessage = mComtText.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        SessionMessage message = new  SessionMessage();
        message.setSid(sid);
        message.setType(answer);
        String Body[]={"",strMessage};
        message.setBody(Body);
        sendCom(message);
    }

    private void sendCom(SessionMessage  message) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().publishSessionMessage(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseSendCom,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseSendCom(Response response) {
        mComtText.setText(null);
    }


    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

//    /**
//     * Shows the soft keyboard
//     */
//    public void showSoftKeyboard(View view) {
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        view.requestFocus();
//        inputMethodManager.showSoftInput(view, 0);
//    }

    public void addRate(String msgid,int rate) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().rateMessage(sid,msgid,rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
