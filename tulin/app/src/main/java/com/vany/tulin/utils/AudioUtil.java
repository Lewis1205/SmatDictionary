package com.vany.tulin.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by van元 on 2017/1/26.
 */

public class AudioUtil {
    private static MediaPlayer mediaPlayer;

    public static void open(String url) {
        if (url == null||url.equals("")) return;
        if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
