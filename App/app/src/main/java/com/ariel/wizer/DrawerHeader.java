package com.ariel.wizer;

import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.squareup.picasso.Picasso;

@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {

    @View(R.id.profileImageView)
    private ImageView profileImage;

    @View(R.id.nameTxt)
    private TextView nameTxt;

    @View(R.id.emailTxt)
    private TextView emailTxt;

    private String displayName;
    private String email;
    private String pic;

    public ImageView getProfileImage() {
        return profileImage;
    }

    public TextView getNameTxt() {
        return nameTxt;
    }

    public DrawerHeader(String _displayName, String _email, String _pic) {
        displayName = _displayName;
        email = _email;
        pic = _pic;
    }

    @Resolve
    private void onResolved() {
        nameTxt.setText(displayName);
        emailTxt.setText(email);
        if(pic!=null&&!(pic.isEmpty()))
            Picasso.get().load(pic).into(profileImage);
    }

}