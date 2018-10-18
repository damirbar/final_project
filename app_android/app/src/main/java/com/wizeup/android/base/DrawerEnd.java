package com.wizeup.android.base;

import android.widget.TextView;

import com.wizeup.android.BuildConfig;
import com.wizeup.android.R;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

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
