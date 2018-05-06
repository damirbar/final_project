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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
import com.ariel.wizer.chat.ChannelsAdapter;
import com.ariel.wizer.chat.ChatActivity;
import com.ariel.wizer.model.ChatChannel;
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
    private ListView messagesList;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private TextView mTvNoResults;
    private ImageView btnLike;
    private FloatingActionButton mFBPost;
    private String sid;
//    private final int delay = 5000; //milliseconds
    private SessionMessagesAdapter mAdapter;
    private SessionMessage[] savePosts;


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
//        classAvgProcess();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            public void run(){
//                classAvgProcess();
//                handler.postDelayed(this, delay);
//            }
//        }, delay);


        messagesList.setOnItemClickListener((parent, view1, position, id) -> {
            long viewId = view1.getId();
            String guid = savePosts[position].get_id();
            if (viewId == R.id.direct_btn_post_like) {
                Toast.makeText(this.getActivity(), "Like", Toast.LENGTH_SHORT).show();
                addRate(guid,1);
                savePosts[position].setRating(savePosts[position].getRating()+1);
                mAdapter.notifyDataSetChanged();

            }
            else if(viewId == R.id.direct_btn_dislike) {
                Toast.makeText(this.getActivity(), "DisLike", Toast.LENGTH_SHORT).show();
                addRate(guid,-1);
                savePosts[position].setRating(savePosts[position].getRating()-1);///change to +1
                mAdapter.notifyDataSetChanged();
            }
            else{
                SessionMessage msg = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(),CommentActivity.class);
                intent.putExtra("sid",sid);///change to guid
//                intent.putExtra("msg",msg);
                startActivity(intent);
            }
        });

        return view;
    }



    private void initViews(View v) {
//        mTvClassAvg = (TextView) findViewById(R.id.tVclassAvg);
        mTvNoResults = (TextView) v.findViewById(R.id.tv_no_results);
        messagesList = (ListView) v.findViewById(R.id.sessionMessagesList);
        btnLike = (ImageView) v.findViewById(R.id.direct_btn_like);

        mFBPost = (FloatingActionButton) v.findViewById(R.id.fb_post);
        mFBPost.setOnClickListener(view -> openPost());
//        btnLike.setOnClickListener(view -> addLike());

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
            mTvNoResults.setVisibility(View.VISIBLE);
        }
        else{
            savePosts = session.getMessages();
            mTvNoResults.setVisibility(View.GONE);
            mAdapter = new SessionMessagesAdapter(this.getActivity(), new ArrayList<>(Arrays.asList(session.getMessages())));
            messagesList.setAdapter(mAdapter);
        }
    }


    public void addRate(String msgid,int rate) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().rateMessage(sid,msgid,rate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
//        mSocket.disconnect();
//        mSocket.off(Socket.EVENT_CONNECT, onConnect);
    }
}
