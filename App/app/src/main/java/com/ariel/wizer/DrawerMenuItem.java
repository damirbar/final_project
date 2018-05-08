package com.ariel.wizer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.chat.ChatChannelsActivity;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.session.ConnectSessionActivity;
import com.ariel.wizer.utils.Constants;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_PROFILE = 1;
    public static final int DRAWER_MENU_ITEM_SESSION = 2;
    public static final int DRAWER_MENU_ITEM_GROUPS = 3;
    public static final int DRAWER_MENU_ITEM_MESSAGE = 4;
    public static final int DRAWER_MENU_ITEM_NOTIFICATIONS = 5;
    public static final int DRAWER_MENU_ITEM_SETTINGS = 6;
    public static final int DRAWER_MENU_ITEM_TERMS = 7;
    public static final int DRAWER_MENU_ITEM_LOGOUT = 8;

    private int mMenuPosition;
    private Activity mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.itemNameTxt)
    private TextView itemNameTxt;

    @View(R.id.itemIcon)
    private ImageView itemIcon;

    public DrawerMenuItem(Activity context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;

    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                itemNameTxt.setText("Profile");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_account_circle_black_18dp));
                break;
            case DRAWER_MENU_ITEM_SESSION:
                itemNameTxt.setText("Session");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_compare_arrows_black_18dp));
                break;
            case DRAWER_MENU_ITEM_GROUPS:
                itemNameTxt.setText("Groups");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_group_work_black_18dp));
                break;
            case DRAWER_MENU_ITEM_MESSAGE:
                itemNameTxt.setText("Messages");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_email_black_18dp));
                break;
            case DRAWER_MENU_ITEM_NOTIFICATIONS:
                itemNameTxt.setText("Notifications");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notifications_black_18dp));
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                itemNameTxt.setText("Settings");
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_settings_black_18dp));
                break;
            case DRAWER_MENU_ITEM_TERMS:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_book_black_18dp));
                itemNameTxt.setText("Terms");
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_exit_to_app_black_18dp));
                itemNameTxt.setText("Logout");
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick(){
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                mContext.startActivity(new Intent (mContext, EditProfileActivity.class));
                if(mCallBack != null)mCallBack.onProfileMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SESSION:
                mContext.startActivity(new Intent (mContext, ConnectSessionActivity.class));
                if(mCallBack != null)mCallBack.onSessionMenuSelected();
                break;
            case DRAWER_MENU_ITEM_GROUPS:
                Toast.makeText(mContext, "Groups", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onGroupsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_MESSAGE:
                mContext.startActivity(new Intent (mContext, ChatChannelsActivity.class));
                if(mCallBack != null)mCallBack.onMessagesMenuSelected();
                break;
            case DRAWER_MENU_ITEM_NOTIFICATIONS:
                Toast.makeText(mContext, "Notifications", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onNotificationsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                Toast.makeText(mContext, "Settings", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onSettingsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_TERMS:
                mContext.startActivity(new Intent (mContext, TermsActivity.class));
                if(mCallBack != null)mCallBack.onTermsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                logout(mContext);
                if(mCallBack != null)mCallBack.onLogoutMenuSelected();
                break;
        }

    }

    private void logout(Activity mActivity) {
        RetrofitRequests mRetrofitRequests = new RetrofitRequests(mContext);
        SharedPreferences.Editor editor = mRetrofitRequests.getmSharedPreferences().edit();
        editor.putString(Constants.PASS,"");
        editor.putString(Constants.TOKEN,"");
        editor.apply();
        Intent intent = new Intent(mActivity, MainActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish();
    }


    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack{
        void onProfileMenuSelected();
        void onSessionMenuSelected();
        void onGroupsMenuSelected();
        void onMessagesMenuSelected();
        void onNotificationsMenuSelected();
        void onSettingsMenuSelected();
        void onTermsMenuSelected();
        void onLogoutMenuSelected();
    }
}