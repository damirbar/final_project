package com.ariel.wizer.session;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.ariel.wizer.R;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WINDOW_SERVICE;

public class VideoPlayerFragment extends Fragment {

    private VideoView vid;
    private String url = Constants.BASE_URL + "sessions/getVideo?sid=";
    private RetrofitRequests mRetrofitRequests;
    private String sid;
    private MediaController mMediaController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_video_player, container, false);
        mRetrofitRequests = new RetrofitRequests(this.getActivity());

        initViews(view);



        if (getData()) {
            playVideo();
        }
        return view;

    }

    private void initViews(View v) {
        vid = v.findViewById(R.id.videoView);

    }

    public VideoView getVid() {
        return vid;
    }

    public MediaController getmMediaController() {
        return mMediaController;
    }

    private boolean getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sid = bundle.getString("sid");
            if(sid != null) {
                url = url + sid;
                return true;
            } else
                return false;
        }
        return false;
    }

    private void playVideo() {
        Map<String, String> header = new HashMap<String, String>(1);
        header.put(Constants.TOKEN_HEADER, mRetrofitRequests.getmToken());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            vid.setVideoURI(Uri.parse(url), header);
        } else {
            Method setVideoURIMethod = null;
            try {
                setVideoURIMethod = vid.getClass().getMethod("setVideoURI", Uri.class, Map.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                if (setVideoURIMethod != null) {
                    setVideoURIMethod.invoke(vid, Uri.parse(url), header);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }


//        String fullScreen =  getActivity().getIntent().getStringExtra("fullScreenInd");
//        if("y".equals(fullScreen)){
//            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//        }
        mMediaController = new FullScreenMediaController(getActivity());

        vid.setMediaController(mMediaController);
    }

//    private boolean isLandScape(){
//        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE))
//                .getDefaultDisplay();
//        int rotation = display.getRotation();
//
//        if (rotation == Surface.ROTATION_90
//                || rotation == Surface.ROTATION_270) {
//            return true;
//        }
//        return false;
//    }
}
