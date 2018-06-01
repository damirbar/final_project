package com.ariel.wizer.demo;

import android.app.Fragment;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
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
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
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

        url = "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4";//rm

        Map<String, String> header = new HashMap<String, String>(1);
        header.put(Constants.TOKEN_HEADER, mRetrofitRequests.getToken());

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

//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

//        String fullScreen =  getActivity().getIntent().getStringExtra("fullScreenInd");
//        if("y".equals(fullScreen)){
//            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//        }



        mMediaController = new MediaController(getActivity());

        vid.setMediaController(mMediaController);
    }

    private boolean isLandScape(){
        Display display = ((WindowManager) getActivity().getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();
        int rotation = display.getRotation();

        if (rotation == Surface.ROTATION_90
                || rotation == Surface.ROTATION_270) {
            return true;
        }
        return false;
    }
}
