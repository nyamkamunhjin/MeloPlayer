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

import java.io.IOException;

import static com.munhj.meloplayer.MusicNotification.CHANNEL_ID;

public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnCompletionListener {
    private String songName;
    private String songPath;
    private String artist;
    private MediaPlayer mediaPlayer;
    private boolean isPaused;
    private Notification notification;
    private PendingIntent pendingIntent;

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





        Intent notificationIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        loadUI();
        isPaused = false;
        startMediaPlayer();


        return START_NOT_STICKY;

    }

    public void loadUI() {
        if(MainActivity.item != null) {
            songName = MainActivity.item.getSongName();
            artist = MainActivity.item.getArtist();
            songPath = MainActivity.item.getSongPath();
        }

        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songName)
                .setContentText(artist)
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(MainActivity.item.getPos() != MainActivity.musicHandler.listItems.size() - 1)
            MainActivity.item = MainActivity.musicHandler.listItems.get(MainActivity.item.getPos() + 1);
        else
            MainActivity.item = MainActivity.musicHandler.listItems.get(0);


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
    }

    public void startMusic() {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        // Request focus for music stream and pass AudioManager.OnAudioFocusChangeListener
        // implementation reference
        int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {
            // Play
            if(mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPaused = false;
            }
        }
    }

    public void prevMusic() {
        if(MainActivity.item.getPos() != 0)
            MainActivity.item = MainActivity.musicHandler.listItems.get(MainActivity.item.getPos() - 1);
        else
            MainActivity.item = MainActivity.musicHandler.listItems.get(MainActivity.musicHandler.listItems.size() - 1);

        loadUI();
        startMediaPlayer();

    }

    public void nextMusic() {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1);

        if(MainActivity.item.getPos() != MainActivity.musicHandler.listItems.size() - 1)
            MainActivity.item = MainActivity.musicHandler.listItems.get(MainActivity.item.getPos() + 1);
        else
            MainActivity.item = MainActivity.musicHandler.listItems.get(0);


        loadUI();
        startMediaPlayer();


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

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
        {
            // Pause
            mediaPlayer.pause();
        }
        else if(focusChange == AudioManager.AUDIOFOCUS_GAIN)
        {
            // Resume

        }
        else if(focusChange == AudioManager.AUDIOFOCUS_LOSS)
        {
            // Stop
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }



}
