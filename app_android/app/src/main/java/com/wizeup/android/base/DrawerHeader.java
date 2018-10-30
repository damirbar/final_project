package com.wizeup.android.base;

import android.widget.ImageView;
import android.widget.TextView;

import com.wizeup.android.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {

    @View(R.id.profileImageView)
    private ImageView profileImage;

    @View(R.id.nameTxt)
    private TextView nameTxt;

    @View(R.id.emailTxt)
    private TextView emailTxt;

    private String email;

    public ImageView getProfileImage() {
        return profileImage;
    }

    public TextView getNameTxt() {
        return nameTxt;
    }

    public DrawerHeader(String _email) {
        email = _email;
    }

    @Resolve
    private void onResolved() {
        emailTxt.setText(email);
    }

}