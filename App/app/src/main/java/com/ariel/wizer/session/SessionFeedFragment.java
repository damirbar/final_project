package com.ariel.wizer.session;

import android.app.Fragment;

//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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

public class SessionFeedFragment extends Fragment {

    public static final String TAG = SessionFeedFragment.class.getSimpleName();


    private CompositeSubscription mSubscriptions;
    private Button askButton;
    private RatingBar simpleRatingBar;
    private TextView mTvClassAvg;
    private TextView mTvClassPin;
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
//    private TextView mTvNoResults;
    private LinearLayout mtaskBar;
    private ImageButton mMessageButton;
    private EditText mMessage;
    private FloatingActionButton mFBPost;

    private final String question = "question";
    private String sid;
    private final int delay = 5000; //milliseconds
    private SessionMessagesAdapter mAdapter;
    private SessionMessage sessionMessage;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_feed,container,false);
        getSid();
        initViews(view);
//        mSocket.on(Socket.EVENT_CONNECT,onConnect);
//        mSocket.connect();

        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(getActivity().findViewById(R.id.session_activity));
        pullMessages();
//        mServerResponse.showSnackBarMessage(sid);
//        classAvgProcess();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                classAvgProcess();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);

        return view;
    }

    private void initViews(View v) {
//        mTvClassPin = (TextView) v.findViewById(R.id.num_sid);
//        mTvClassPin.setText(pin);
//        mTvClassAvg = (TextView) findViewById(R.id.tVclassAvg);
//        simpleRatingBar = (RatingBar) findViewById(R.id.ratingBar);
//        mtaskBar = (LinearLayout) findViewById(R.id.taskBar);
//        mMessageButton = (ImageButton) findViewById(R.id.sendChatMessageButton);
//        askButton = (Button) findViewById(R.id.ask_Button);
//        mTvNoResults = (TextView) v.findViewById(R.id.tv_no_results);
        messagesList = (ListView) v.findViewById(R.id.sessionMessagesList);
        mFBPost = (FloatingActionButton) v.findViewById(R.id.fb_post);
        mFBPost.setOnClickListener(view -> openPost());

//        mMessage = (EditText) v.findViewById(R.id.chatMessageEditText);
//        askButton.setOnClickListener(view -> openQuestion());
    }


//    private Socket mSocket;
//    {
//        try {
//            mSocket = IO.socket(Constants.BASE_URL);
//        } catch (URISyntaxException e) {}
//    }


    private void openPost(){
        Intent intent = new Intent(getActivity(), PostActivity.class);
        startActivity(intent);
    }


    private void getSid() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
        }
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
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getMessages(sid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponsePull,i -> mServerResponse.handleError(i)));
    }

    private void handleResponsePull(Session session) {
        if(session.getMessages().length == 0){
//            mTvNoResults.setVisibility(View.VISIBLE);
        }
        else{
//            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SessionMessagesAdapter(this.getActivity(), new ArrayList<>(Arrays.asList(session.getMessages())));
            messagesList.setAdapter(mAdapter);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
//        mSocket.disconnect();
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
    }
}
