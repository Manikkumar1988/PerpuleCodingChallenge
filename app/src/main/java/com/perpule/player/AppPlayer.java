package com.perpule.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import com.perpule.data.Song;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mani on 16/12/17.
 */

public class AppPlayer implements PlayerInteractor, MediaPlayer.OnPreparedListener {

  MediaPlayer mediaPlayer;
  Context context;
  public AppPlayer(Context context) {

    this.context = context;
    initMediaPlayer();
  }

  private void initMediaPlayer() {
    if (mediaPlayer == null) {
      mediaPlayer = new MediaPlayer();//new MediaPlayer instance
    }

    //Set up MediaPlayer event listeners
    mediaPlayer.setOnPreparedListener(this);
    //Reset so that the MediaPlayer is not pointing to another data source
    mediaPlayer.reset();


    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
  }

  @Override public void prepare() {

  }

  @Override
  public void play(Song song) {
    try {
      mediaPlayer.reset();

      File file = getLocalFilePath(song);

      mediaPlayer.setDataSource(file.getAbsolutePath());
      mediaPlayer.prepare();
      mediaPlayer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @NonNull private File getLocalFilePath(Song song) throws MalformedURLException {
    URL url = new URL(song.getAudio());

    String[] segments = url.getPath().split("/");
    String idStr = segments[segments.length-1];

    return new File(context.getCacheDir(), idStr);
  }

  @Override public void pause() {
    mediaPlayer.pause();
  }

  @Override public void release() {
    mediaPlayer.release();
    mediaPlayer = null;
  }

  @Override public void seek(){
  }

  @Override public void onPrepared(MediaPlayer mediaPlayer) {

  }
}
