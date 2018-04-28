package com.ariel.wizer;

import android.net.Uri;

import java.io.File;

public class Utils {

    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
