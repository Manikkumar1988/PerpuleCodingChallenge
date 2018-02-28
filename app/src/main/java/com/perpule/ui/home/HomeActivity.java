package com.perpule.ui.home;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import com.google.gson.Gson;
import com.perpule.R;
import com.perpule.data.RootObject;
import com.perpule.data.Song;
import com.perpule.player.PlayerService;
import com.perpule.ui.AppViewModel;
import com.perpule.util.Util;

public class HomeActivity extends LifecycleActivity{

  public static int counter = 0;
  private boolean mIsPlayerServiceBound = false;
  private PlayerService playerService;
  AppViewModel appViewModel;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

    setContentView(R.layout.activity_home);

    Transition explodeAnimation = TransitionInflater.from(this).inflateTransition(R.transition.explode);
    explodeAnimation.setDuration(1000);
    getWindow().setEnterTransition(explodeAnimation);

    initializeViewModel();

    listenForPlayEvent();

    listenForContinueEvent();
  }

  private void listenForContinueEvent() {
    appViewModel.getContinueEvent().observe(this, new Observer<Void>() {
      @Override public void onChanged(@Nullable Void aVoid) {
        addFragment(R.id.content,new ItemFragment(),ItemFragment.FRAGMENT_TAG);
      }
    });
  }

  private void listenForPlayEvent() {
    appViewModel.getPlayEvent().observe(this, new Observer<Song>() {
      @Override public void onChanged(@Nullable Song song) {
        playerService.play(song);
      }
    });
  }

  public void addFragment(@IdRes final int containerViewId,
      @NonNull final Fragment fragment,
      @NonNull final String fragmentTag) {

    //TODO: Refactor here
    new AsyncTask<Void,Void,Void>() {

      @Override protected Void doInBackground(Void... voids) {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("A",MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString("MyObject", "");
        RootObject obj = gson.fromJson(json, RootObject.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("CurrentObject",obj.getData().get(counter%6));
        bundle.putSerializable("NextObject",obj.getData().get((counter+1)%6));

        fragment.setArguments(bundle);
        return null;
      }

      @Override protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left)
            .replace(containerViewId, fragment, fragmentTag)
            .disallowAddToBackStack()
            .commit();

        counter++;
      }
    }.execute();

  }


  public void initializeViewModel() {
    appViewModel =
        ViewModelProviders.of(this).get(AppViewModel.class);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mIsPlayerServiceBound) {
      unbindService(serviceConnection);
      //service is active
      playerService.stopSelf();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    //Check is service is active
    if (!mIsPlayerServiceBound) {
      Intent playerIntent = new Intent(this, PlayerService.class);
      startService(playerIntent);
      bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
      Util.Logger("Service bind success");
    } else {
      //Service is active
      Util.Logger("Service is active");

      addFragment(R.id.content,
          new ItemFragment(),
          ItemFragment.FRAGMENT_TAG);
    }
  }

  //Binding this Client to the AudioPlayer Service
  private ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      // We've bound to LocalService, cast the IBinder and get LocalService instance
      PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
      playerService = binder.getService();
      mIsPlayerServiceBound = true;

      Util.Logger("Service bound");

      Util.Logger("onServiceConnected -> calling to addFragment");
      addFragment(R.id.content,
          new ItemFragment(),
          ItemFragment.FRAGMENT_TAG);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      mIsPlayerServiceBound = false;
    }
  };


  @Override protected void onPause() {
    super.onPause();
    playerService.pause();
    counter = 0;
  }
}
