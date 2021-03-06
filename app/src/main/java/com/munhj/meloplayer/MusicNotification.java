package com.munhj.meloplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MusicNotification extends Application {
    public static final String CHANNEL_ID = "musicServiceChannel";
    private static boolean isBackFromNotification = false;


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    public static boolean isIsBackFromNotification() {
        return isBackFromNotification;
    }

    public static void setIsBackFromNotification(boolean isBackFromNotification) {
        MusicNotification.isBackFromNotification = isBackFromNotification;
    }
}
