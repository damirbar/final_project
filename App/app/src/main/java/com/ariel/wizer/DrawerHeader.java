package com.ariel.wizer;

import android.widget.ImageView;
import android.widget.TextView;

import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

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

    @Resolve
    private void onResolved() {
        nameTxt.setText("Shay Cohen");
        emailTxt.setText(Constants.EMAIL);
    }

}