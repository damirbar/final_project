package com.ariel.wizer.base;

import android.widget.TextView;

import com.ariel.wizer.BuildConfig;
import com.ariel.wizer.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import static com.ariel.wizer.BuildConfig.*;

@NonReusable
@Layout(R.layout.drawer_end)
public class DrawerEnd {

    @View(R.id.verTxt)
    private TextView version;

    @Resolve
    private void onResolved() {
        String versionName = "v" + BuildConfig.VERSION_NAME;
        version.setText(versionName);
    }


}
