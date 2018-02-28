package com.perpule.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.perpule.data.Song;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PlayerService extends Service implements PlayerInteractor{

  // Binder given to clients
  private final IBinder iBinder = new LocalBinder();

  @Override
  public IBinder onBind(Intent intent) {
    return iBinder;
  }

  private AppPlayer appPlayer;
  ThreadPoolExecutor executor;

  @Override public void onCreate() {
    super.onCreate();
    appPlayer = new AppPlayer(getApplicationContext());

    int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    executor = new ThreadPoolExecutor(
        NUMBER_OF_CORES*2,
        NUMBER_OF_CORES*2,
        60L,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>()
    );

  }

  @Override public void prepare() {

  }

  @Override public void play(final Song song) {
    executor.execute(new Runnable() {
      @Override public void run() {
        appPlayer.play(song);

      }
    });
  }

  @Override public void pause() {
    appPlayer.release();
  }

  @Override public void release() {

  }

  @Override public void seek() {

  }

  public class LocalBinder extends Binder {
    public PlayerService getService() {
      return PlayerService.this;
    }
  }
}
