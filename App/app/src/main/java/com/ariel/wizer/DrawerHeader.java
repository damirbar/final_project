package com.ariel.wizer;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.squareup.picasso.Picasso;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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


    public DrawerHeader(String _displayName, String _email) {
        displayName = _displayName;
        email = _email;
    }

    @Resolve
    private void onResolved() {
        nameTxt.setText(displayName);
        emailTxt.setText(email);
        Picasso.get().load("https://scontent.fsdv2-1.fna.fbcdn.net/v/t1.0-9/22730135_839880432861616_8306732533151605396_n.jpg?_nc_cat=0&oh=1597bce378ebaa853df982319d669bf5&oe=5B9303BC").into(profileImage);

    }

}