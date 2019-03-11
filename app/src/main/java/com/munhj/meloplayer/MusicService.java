package com.munhj.meloplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;
import java.io.IOException;


import ServiceCallbacks.ServiceCallbacks;

import static com.munhj.meloplayer.MainActivity.item;
import static com.munhj.meloplayer.MusicNotification.CHANNEL_ID;

public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
    private String songName;
    private String songPath;
    private String artist;
    private MediaPlayer mediaPlayer;
    private boolean isPaused;
    private PendingIntent pendingIntent;
    private ServiceCallbacks serviceCallbacks;
    private RemoteViews collapsedView;

    //Bound service
    private final IBinder binder = new LocalBinder();



    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("extras", intent.getExtras().getString("stop_service"));
        if(intent.hasExtra("key")) {
            switch(intent.getExtras().getString("key")) {
                case "stop_service":
                    stopMusicService();
                    break;
                case "play_music":
                    // if app is opened at the moment
                    if(serviceCallbacks != null) {
                        if(mediaPlayer.isPlaying()) serviceCallbacks.pauseMusic();
                        else serviceCallbacks.startMusic();
                    } else {
                        if(mediaPlayer.isPlaying()) pauseMusic();
                        else startMusic();
                    }
                    break;
                case "next_music":
                    if(serviceCallbacks != null) {
                        serviceCallbacks.nextMusic();
                    } else {
                        nextMusic();
                        startMusic();
                    }

                    break;
                case "prev_music":
                    if(serviceCallbacks != null) {
                        serviceCallbacks.prevMusic();
                    } else {
                        prevMusic();
                        startMusic();
                    }
                    break;
            }
        } else {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

            loadUI();
            isPaused = false;
            startMediaPlayer();
        }

        return START_NOT_STICKY;

    }

    public void stopMusicService() {
        Toast.makeText(this, "stopping", Toast.LENGTH_LONG).show();
        stopForeground(true);
        stopSelf();
        onDestroy();
    }

    public void showNotification() {
        collapsedView = new RemoteViews(getPackageName(),
                R.layout.notification_collapsed);

        collapsedView.setImageViewBitmap(R.id.image_view_notification, item.getAlbumCover());
        collapsedView.setTextViewText(R.id.songName_notification, songName);
        collapsedView.setTextViewText(R.id.artist_notification, artist);

        if(isPaused())
            collapsedView.setImageViewResource(R.id.playButton_notification, R.drawable.ic_play);
        else
            collapsedView.setImageViewResource(R.id.playButton_notification, R.drawable.ic_pause);

        collapsedView.setOnClickPendingIntent(R.id.stopButton_notification,
                PendingIntent.getBroadcast(
                        this,
                        0,
                        (new Intent(this, NotificationReceiver.class)).setAction("stop"),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        collapsedView.setOnClickPendingIntent(R.id.playButton_notification,
                PendingIntent.getBroadcast(
                        this,
                        0,
                        (new Intent(this, NotificationReceiver.class)).setAction("play"),
                        PendingIntent.FLAG_CANCEL_CURRENT));

        collapsedView.setOnClickPendingIntent(R.id.prevButton_notification,
                PendingIntent.getBroadcast(
                        this,
                        0,
                        (new Intent(this, NotificationReceiver.class)).setAction("prev"),
                        PendingIntent.FLAG_CANCEL_CURRENT));

        collapsedView.setOnClickPendingIntent(R.id.nextButton_notification,
                PendingIntent.getBroadcast(
                this,
                0,
                (new Intent(this, NotificationReceiver.class)).setAction("next"),
                PendingIntent.FLAG_CANCEL_CURRENT));

        // build notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setCustomBigContentView(collapsedView)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    public void loadUI() {
        if(MainActivity.item != null) {
            songName = MainActivity.item.getSongName();
            artist = MainActivity.item.getArtist();
            songPath = MainActivity.item.getSongPath();
        }

        showNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        System.exit(0);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(MainActivity.item.getPos() != MainActivity.getListItems().size() - 1) {
            MainActivity.item = MainActivity
                    .getListItems()
                    .get(MainActivity.item.getPos() + 1);
        }
        else {
            MainActivity.item = MainActivity.getListItems().get(0);
        }

        loadUI();
        startMediaPlayer();
    }


    public void startMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseMusic() {
        if(mediaPlayer != null) {
            mediaPlayer.pause();
            isPaused = true;
        }
        loadUI();
    }

    public void startMusic() {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        // Request focus for music stream and pass AudioManager.OnAudioFocusChangeListener
        // implementation reference
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Play
            if(mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPaused = false;
            }
        }
        loadUI();
    }

    public void prevMusic() {
        if(MainActivity.item.getPos() != 0) {
            MainActivity.item = MainActivity
                    .getListItems()
                    .get(MainActivity.item.getPos() - 1);
        }
        else {
            MainActivity.item = MainActivity
                    .getListItems()
                    .get(MainActivity.getListItems().size() - 1);
        }

        loadUI();
        startMediaPlayer();

//        if(serviceCallbacks != null) serviceCallbacks.loadUI();
//        startMusic();
    }

    public void nextMusic() {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1);

        if(MainActivity.item.getPos() != MainActivity.getListItems().size() - 1) {
            MainActivity.item = MainActivity
                    .getListItems()
                    .get(MainActivity.item.getPos() + 1);
        }
        else {
            MainActivity.item = MainActivity.getListItems().get(0);
        }


        loadUI();
        startMediaPlayer();
//        if(serviceCallbacks != null) serviceCallbacks.loadUI();
//        startMusic();


    }
    public boolean isNull() {
        return mediaPlayer == null;
    }
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }
    public int getDuration() {
        return mediaPlayer.getDuration();
    }
    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }
    public boolean isPaused() {
        return isPaused;
    }
    public void setCallbacks(ServiceCallbacks callbacks) {
        this.serviceCallbacks = callbacks;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause
            if(serviceCallbacks != null)
                serviceCallbacks.pauseMusic();
            else
                pauseMusic();
        }
        else if(focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // Resume

        }
        else if(focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // Pause or stop
            if(serviceCallbacks != null)
                serviceCallbacks.pauseMusic();
            else
                pauseMusic();

        }
    }



}
