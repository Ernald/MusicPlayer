package com.gohool.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AndroidException;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private TextView leftTime;
    private TextView rightTime;
    private Button previousButt;
    private Button playButt;
    private Button nextButt;
    private ImageView profilePic;
    private SeekBar seekBarMusic;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBarMusic.setMax(mediaPlayer.getDuration());
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    mediaPlayer.seekTo(i);
                }

                SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                leftTime.setText(timeFormat.format(new Date(currentPos)));
                rightTime.setText(timeFormat.format(new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        previousButt.setOnClickListener(this);
        playButt.setOnClickListener(this);
        nextButt.setOnClickListener(this);

    }

    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.la_cocaina);

        leftTime = (TextView) findViewById(R.id.leftTimeId);
        rightTime = (TextView) findViewById(R.id.rightTimeId);
        previousButt = (Button) findViewById(R.id.prevButton);
        playButt = (Button) findViewById(R.id.playButton);
        nextButt = (Button) findViewById(R.id.nextButton);
        profilePic = (ImageView) findViewById(R.id.imageSongId);
        seekBarMusic = (SeekBar) findViewById(R.id.seekBarId);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.prevButton:
                //Code
                backMusic();
                break;
            case R.id.playButton:
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                }else {
                    playMusic();
                }
                break;
            case R.id.nextButton:
                //Code
                nextMusic();
                break;
        }
    }

    public void playMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
            playButt.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            playButt.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void backMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic(){
        if (mediaPlayer.isPlaying()){
         mediaPlayer.seekTo(mediaPlayer.getDuration()-1000);
        }
    }

    public void updateThread(){
        thread = new Thread(){
            @Override
            public void run() {
                try {
                        while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            Thread.sleep(50);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    int newPosition = mediaPlayer.getCurrentPosition();
                                    int newDuration = mediaPlayer.getDuration();
                                    seekBarMusic.setMax(newDuration);
                                    seekBarMusic.setProgress(newPosition);

                                    //Update the text

                                    leftTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                    .format(new Date(mediaPlayer.getCurrentPosition()))));
                                    rightTime.setText(String.valueOf(new java.text.SimpleDateFormat("mm:ss")
                                            .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));


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