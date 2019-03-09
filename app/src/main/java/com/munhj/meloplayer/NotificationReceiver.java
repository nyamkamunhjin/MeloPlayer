package com.munhj.meloplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
            switch(intent.getAction()) {
                case "stop":
                    stopMusicService(context);
                    break;
                case "play":
                    playMusicService(context);
                    break;
                case "next":
                    nextMusicService(context);
                    break;
                case "prev":
                    prevMusicService(context);
                    break;
            }

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.cancel(1);
    }

    private void nextMusicService(Context context) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("key", "next_music");
        context.startService(serviceIntent);
    }

    private void prevMusicService(Context context) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("key", "prev_music");
        context.startService(serviceIntent);
    }

    private void playMusicService(Context context) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("key", "play_music");
        context.startService(serviceIntent);
    }

    private void stopMusicService(Context context) {
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("key", "stop_service");
        context.startService(serviceIntent);
    }

}
