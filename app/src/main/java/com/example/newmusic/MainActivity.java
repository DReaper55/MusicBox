package com.example.newmusic;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MediaPlayer mediaPlayer;
    private ImageView snippet;
    private TextView trackName;
    private TextView artistName;
    private TextView leftLength;
    private TextView rightLength;
    private SeekBar mySeek;
    private Button previous;
    private Button playButt;
    private Button nextButt;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        mySeek = findViewById(R.id.seekBar);

        // Set the seekbar to the duration of the track
        mySeek.setMax(mediaPlayer.getDuration());
        mySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    // seek music to the current progress of the track

                    mediaPlayer.seekTo(progress);
                }

                // setting the track times to proper formats
                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftLength.setText(dateFormat.format(new Date(currentPos)));
                rightLength.setText(dateFormat.format(new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpUI(){
        snippet = findViewById(R.id.snippetID);
        leftLength = findViewById(R.id.leftLengthID);
        rightLength = findViewById(R.id.rightLengthID);
        mySeek = findViewById(R.id.seekBar);
        previous = findViewById(R.id.previousID);
        playButt = findViewById(R.id.playID);
        nextButt = findViewById(R.id.nextID);
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.entertainer);

        previous.setOnClickListener(this);
        playButt.setOnClickListener(this);
        nextButt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.previousID:

                // restart song
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(0);
                }
                break;

                // pause or resume track
            case R.id.playID:
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                } else{
                    startMusic();
                }
                break;

            case R.id.nextID:

                // goto end of track
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
                }
                break;
        }
    }

    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            playButt.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playButt.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

   public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                try{
                    // a track must be loaded and playing to run the thread

                    while(mediaPlayer != null && mediaPlayer.isPlaying()){
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int currentPos = mediaPlayer.getCurrentPosition();
                                int setMax = mediaPlayer.getDuration();
                                mySeek.setProgress(currentPos);
                                mySeek.setMax(setMax);

                                leftLength.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                        .format(mediaPlayer.getCurrentPosition())));
                                rightLength.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                        .format(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition())));
                            }
                        });
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
   }

    @Override
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;

        super.onDestroy();
    }
}
