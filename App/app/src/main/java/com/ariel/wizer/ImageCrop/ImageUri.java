package com.ariel.wizer.ImageCrop;

import android.net.Uri;

import java.io.File;

public class ImageUri {

    public static Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }
}
