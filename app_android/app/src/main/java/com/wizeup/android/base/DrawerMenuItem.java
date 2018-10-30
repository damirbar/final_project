package com.wizeup.android.base;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wizeup.android.R;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_PROFILE = 1;
    public static final int DRAWER_MENU_ITEM_SESSION = 2;
    public static final int DRAWER_MENU_ITEM_MY_COURSES = 3;
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
        mCallBack = (DrawerCallBack) context;
    }

    @Resolve
    private void onResolved() {
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                itemNameTxt.setText(R.string.profile);
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_account_circle_black_18dp));
                break;
            case DRAWER_MENU_ITEM_SESSION:
                itemNameTxt.setText(R.string.session);
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_compare_arrows_black_18dp));
                break;
            case DRAWER_MENU_ITEM_MY_COURSES:
                itemNameTxt.setText(R.string.my_courses);
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_group_work_black_18dp));
                break;
            case DRAWER_MENU_ITEM_MESSAGE:
                itemNameTxt.setText(R.string.messages);
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_email_black_18dp));
                break;
            case DRAWER_MENU_ITEM_NOTIFICATIONS:
                itemNameTxt.setText(R.string.notifications);
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_notifications_black_18dp));
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                itemNameTxt.setText(R.string.settings);
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_settings_black_18dp));
                break;
            case DRAWER_MENU_ITEM_TERMS:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_book_black_18dp));
                itemNameTxt.setText(R.string.about);
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                itemIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_exit_to_app_black_18dp));
                itemNameTxt.setText(R.string.logout);
                break;
        }
    }

    @Click(R.id.mainView)
    private void onMenuItemClick(){
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                if(mCallBack != null)mCallBack.onProfileMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SESSION:
                if(mCallBack != null)mCallBack.onSessionMenuSelected();
                break;
            case DRAWER_MENU_ITEM_MY_COURSES:
                if(mCallBack != null)mCallBack.onMyCoursesMenuSelected();
                break;
            case DRAWER_MENU_ITEM_MESSAGE:
                Toast.makeText(mContext, "Chat", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onMessagesMenuSelected();
                break;
            case DRAWER_MENU_ITEM_NOTIFICATIONS:
                Toast.makeText(mContext, "Notifications", Toast.LENGTH_SHORT).show();
                if(mCallBack != null)mCallBack.onNotificationsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                mCallBack.onSettingsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_TERMS:
                if(mCallBack != null)mCallBack.onTermsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                if(mCallBack != null)mCallBack.onLogoutMenuSelected();
                break;
        }

    }

    public interface DrawerCallBack{
        void onProfileMenuSelected();
        void onSessionMenuSelected();
        void onMyCoursesMenuSelected();
        void onMessagesMenuSelected();
        void onNotificationsMenuSelected();
        void onSettingsMenuSelected();
        void onTermsMenuSelected();
        void onLogoutMenuSelected();
    }
}