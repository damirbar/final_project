package com.ariel.wizeup.notification;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.ariel.wizeup.R;
import com.ariel.wizeup.entry.EntryActivity;
import com.ariel.wizeup.utils.Constants;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import static com.ariel.wizeup.utils.Constants.BASE_URL;


public class NotificationService extends Service {


    private String mId;
    private Socket mSocket;
    private static int id;
    private int MAX_NOTIFICATIONS = 100;
    private  Context ctx;

    {
        try {
            mSocket = IO.socket(BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        ctx = getApplicationContext();
        initSharedPreferences();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on("newNotification", onNewNotification);
        mSocket.connect();
    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mId = mSharedPreferences.getString(Constants.ID, "");
    }


    public void createNotification(String title, String text) {
        int oneTimeID = (int) SystemClock.uptimeMillis();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.wizeup_logo))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentTitle(title)
                        .setContentText(text);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, EntryActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(EntryActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify("wizeup",oneTimeID, mBuilder.build());
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("registerClientToClients", mId);
        }
    };


    private Emitter.Listener onNewNotification = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = (JSONObject) args[0];

            try {
                String content = data.getString("content");

//                if(isInBackground()){
                    createNotification("wizeUp", content);
//                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public  boolean isInBackground() {
        boolean isInBackground;
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        return isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("newNotification", onNewNotification);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}