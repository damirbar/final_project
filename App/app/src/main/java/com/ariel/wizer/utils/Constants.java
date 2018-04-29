package com.ariel.wizer.utils;

public class Constants {
    //public static final String BASE_URL = "http://10.0.2.2:3000/";//Emulator
    public static final String BASE_URL = "http://192.168.0.46:3000/";

    public static final String TOKEN = "token";
    public static final String EMAIL = "email";
    public static final String PASS = "pass";

    public interface PicModes {
        String CAMERA = "Camera";
        String GALLERY = "Gallery";
    }

    public interface IntentExtras {
        String ACTION_CAMERA = "action-camera";
        String ACTION_GALLERY = "action-gallery";
        String IMAGE_PATH = "image-path";
    }

}
