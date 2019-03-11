package Fragments;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.munhj.meloplayer.MainActivity;
import com.munhj.meloplayer.MusicService;
import com.munhj.meloplayer.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ServiceCallbacks.ServiceCallbacks;

import static com.munhj.meloplayer.MainActivity.item;

public class musicFragment extends Fragment implements View.OnClickListener, ServiceCallbacks {

    private TextView songName, artist, leftTime, rightTime;

    private ImageView albumCover;
    private SeekBar seekBar;

    private Button prevButton, playButton, nextButton;

    private Thread thread;
    private View musicView;
    private BottomNavigationView bottomNavigationView;
    public static boolean isMusicChosen = false;
    private MusicService musicService;
    private boolean mBound = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)  {

        musicView = inflater.inflate(R.layout.activity_music, container, false);
        setUpUI();
        initiateService();

        return musicView;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // when the music service is successfully connected to fragment
            MusicService.LocalBinder binder = ((MusicService.LocalBinder) service);
            musicService = binder.getService();
            mBound = true;
            musicService.setCallbacks(musicFragment.this);
            loadUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };



    private void setUpUI() {
        songName = musicView.findViewById(R.id.song_name);
        artist = musicView.findViewById(R.id.artist_music_layout);
        leftTime = musicView.findViewById(R.id.leftTime);
        rightTime = musicView.findViewById(R.id.rightTime);
        albumCover = musicView.findViewById(R.id.album_cover);
        seekBar = musicView.findViewById(R.id.seekBar);
        prevButton = musicView.findViewById(R.id.prevButton);
        playButton = musicView.findViewById(R.id.playButton);
        nextButton = musicView.findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    private void initiateService() {
        if(isMusicChosen && item != null) {
            Intent serviceIntent = new Intent(musicView.getContext(), MusicService.class);
            serviceIntent.putExtra("stop_service", false);
            musicView.getContext().bindService(serviceIntent,
                    connection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBound) {
            musicService.setCallbacks(musicFragment.this);
            loadUI();
        }
    }

    public void loadUI() {

        if(isMusicChosen && item != null) {
            songName.setText(item.getSongName());
            artist.setText(item.getArtist());
            albumCover.setImageBitmap(item.getAlbumCover());

            if(!musicService.isPaused()) {
                startMusic();
            } else {
                loadPaused();
            }
        }


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    musicService.seekTo(progress);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.US);

                    leftTime.setText(dateFormat
                            .format(new Date(musicService.getCurrentPosition())));
                    rightTime.setText(dateFormat
                            .format(new Date(musicService.getDuration() - musicService.getCurrentPosition())));

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

        });


    }

    // prev, play, next buttons
    @Override
    public void onClick(View v) {
        if(mBound) {
            switch (v.getId()) {
                case R.id.prevButton:
                    prevMusic();
                    break;

                case R.id.playButton:
                    if (musicService.isPlaying())
                        pauseMusic();
                    else
                        startMusic();
                    break;

                case R.id.nextButton:
                    nextMusic();
                    break;
            }
        } else {
            Log.d("mBound: ", "false");
        }
    }



    // Methods for music control
    public void pauseMusic() {
        musicService.pauseMusic();

        if(thread != null) {
            thread.interrupt();
            thread = null;
        }
        playButton.setBackgroundResource(android.R.drawable.ic_media_play);

    }

    public void startMusic() {
        musicService.startMusic();
        updateThread();
        playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    public void prevMusic() {
        musicService.prevMusic();
        loadUI();
        startMusic();
    }

    public void nextMusic() {
        musicService.nextMusic();
        loadUI();
        startMusic();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
            musicService.setCallbacks(null);
        }
    }

    // method to load fragment interface when music is paused and re-opened
    private void loadPaused() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.US);

        leftTime.setText(dateFormat
                .format(new Date(musicService.getCurrentPosition())));
        rightTime.setText(dateFormat
                .format(new Date(musicService.getDuration() - musicService.getCurrentPosition())));

        seekBar.setMax(musicService.getDuration());
        seekBar.setProgress(musicService.getCurrentPosition());
    }

    // seekbar update thread
    public void updateThread()  {
        if(thread != null) {
            thread.interrupt();
            thread = null;
        }
        thread = new Thread() {
            @Override
            public void run() {

                try {
                    while (musicService.isPlaying()) {
                        Thread.sleep(50);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!musicService.isNull() && musicService.isPlaying()) {
                                    int newPosition = musicService.getCurrentPosition();
                                    int newMax = musicService.getDuration();
                                    seekBar.setMax(newMax);
                                    seekBar.setProgress(newPosition);

                                    // update the text
                                    leftTime.setText(String.valueOf(new SimpleDateFormat("mm:ss", Locale.US)
                                            .format(new Date(newPosition))));
                                    rightTime.setText(String.valueOf(new SimpleDateFormat("mm:ss", Locale.US)
                                            .format(new Date(newMax - newPosition))));

                                    if(musicService.getDuration() - musicService.getCurrentPosition() < 1000)
                                        nextMusic();


                                }

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(MainActivity.item != null) {
            musicView.getContext().unbindService(connection);
            mBound = false;
        }

    }

}
