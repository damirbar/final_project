//package com.ariel.wizer.demo;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.widget.MediaController;
//import android.widget.VideoView;
//
//import com.ariel.wizer.R;
//import com.ariel.wizer.network.RetrofitRequests;
//import com.ariel.wizer.utils.Constants;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//public class VideoPlayerActivity extends Activity implements MediaPlayer.OnCompletionListener {
//
//    private VideoView v;
//    private String url = Constants.BASE_URL + "sessions/getVideo?sid=";
//    private RetrofitRequests mRetrofitRequests;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video_player);
//        mRetrofitRequests = new RetrofitRequests(this);
//        initViews();
//
//        if (getData()) {
//            playVideo();
//        }
//        else
//            finish();
//    }
//
//    private void initViews() {
//        v = findViewById(R.id.videoView);
//    }
//
//    private boolean getData() {
//        if (getIntent().getExtras() != null) {
//            String sid = getIntent().getExtras().getString("sid");
//            if(sid != null) {
//                url = url + sid;
//                return true;
//            } else
//                return false;
//        }
//        return false;
//    }
//
//    private void playVideo() {
//        Map<String, String> header = new HashMap<String, String>(1);
//        header.put(Constants.TOKEN_HEADER, mRetrofitRequests.getToken());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            v.setVideoURI(Uri.parse(url), header);
//        } else {
//            Method setVideoURIMethod = null;
//            try {
//                setVideoURIMethod = v.getClass().getMethod("setVideoURI", Uri.class, Map.class);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (setVideoURIMethod != null) {
//                    setVideoURIMethod.invoke(v, Uri.parse(url), header);
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }
//        v.setMediaController(new MediaController(this));
//        v.setOnCompletionListener(this);
//        v.start();
//    }
//
//
//    @Override
//    public void onCompletion(MediaPlayer v) {
//        finish();
//    }
//
//    //Convenience method to show a video
//    public static void showRemoteVideo(Context ctx, String sid) {
//        Intent i = new Intent(ctx, VideoPlayerActivity.class);
//
//        i.putExtra("sid", sid);
//        ctx.startActivity(i);
//    }
//}
