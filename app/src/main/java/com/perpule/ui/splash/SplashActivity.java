package com.perpule.ui.splash;

import android.app.ActivityOptions;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.Gson;
import com.perpule.R;
import com.perpule.data.RootObject;
import com.perpule.downloadmanager.DownloadException;
import com.perpule.downloadmanager.DownloadResultReceiver;
import com.perpule.downloadmanager.DownloadService;
import com.perpule.engine.DataDownloader;
import com.perpule.engine.SongDownloader;
import com.perpule.ui.AppViewModel;
import com.perpule.ui.home.HomeActivity;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;
import java.io.IOException;

public class SplashActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver{

  @BindView(R.id.imageView) ImageView imageView;
  @BindView(R.id.go_button) Button goButton;
  @BindView(R.id.transitions_container) RelativeLayout transitionsContainer;
  @BindView(R.id.txt) TextView loadingText;

  private boolean isLoaded = false;
  AppViewModel appViewModel;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    ButterKnife.bind(this);

    initializeViewModel();

    final Animation animation_1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.rotate);
    final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.antirotate);

    imageView.startAnimation(animation_2);
    animation_2.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        imageView.startAnimation(animation_1);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });

    animation_1.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {

        if(isLoaded) {
          showStart();
        } else {
          imageView.startAnimation(animation_2);
        }
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });


    appViewModel.getRootObject().observe(this, new Observer<RootObject>() {
      @Override public void onChanged(@Nullable RootObject rootObject) {
        checkForAvailabilityFirstSong(rootObject);
      }
    });
  }

  private void checkForAvailabilityFirstSong(RootObject rootObject) {
    String url = rootObject.getData().get(0).getAudio();

    DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
    mReceiver.setReceiver(SplashActivity.this);
    Intent intent = new Intent(SplashActivity.this, DownloadService.class);

        /* Send optional extras to Download IntentService */
    intent.putExtra("url", url);
    intent.putExtra("receiver", mReceiver);
    intent.putExtra("requestId", 101);

    SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("A",MODE_PRIVATE);

    SharedPreferences.Editor prefsEditor = mPrefs.edit();
    Gson gson = new Gson();
    String json = gson.toJson(rootObject); // myObject - instance of MyObject
    prefsEditor.putString("MyObject", json);
    prefsEditor.commit();

    startService(intent);
  }

  public void initializeViewModel() {
    appViewModel =
        ViewModelProviders.of(this).get(AppViewModel.class);

    SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("A",MODE_PRIVATE);

    Gson gson = new Gson();
    String json = mPrefs.getString("MyObject", "");
    RootObject obj = gson.fromJson(json, RootObject.class);

    if(obj == null) {
      appViewModel.downloadData();
    } else {
      checkForAvailabilityFirstSong(obj);
    }
  }

  private void showStart() {

    TransitionSet set = new TransitionSet()
        .addTransition(new Scale(0.7f))
        .addTransition(new Fade())
        .setDuration(750)
        .setInterpolator(true ? new LinearOutSlowInInterpolator() :
            new FastOutLinearInInterpolator());

    TransitionManager.beginDelayedTransition(transitionsContainer, set);
    goButton.setVisibility(true ? View.VISIBLE : View.INVISIBLE);


    TransitionSet set1 = new TransitionSet()
        .addTransition(new Scale(0.7f))
        .addTransition(new Fade())
        .setDuration(750)
        .setInterpolator(false ? new LinearOutSlowInInterpolator() :
            new FastOutLinearInInterpolator());

    TransitionManager.beginDelayedTransition(transitionsContainer, set1);
    imageView.setVisibility(false ? View.VISIBLE : View.INVISIBLE);
    loadingText.setVisibility(false ? View.VISIBLE : View.INVISIBLE);

  }

  public void handleGo(View view) {

    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);

    Intent i = new Intent(getBaseContext(),HomeActivity.class);
    startActivity(i, options.toBundle());

    //finish();
  }

  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {
    switch (resultCode) {
      case DownloadService.STATUS_RUNNING:

        break;
      case DownloadService.STATUS_FINISHED:
        isLoaded = true;

        break;
      case DownloadService.STATUS_ERROR:
                /* Handle the error */
        String error = resultData.getString(Intent.EXTRA_TEXT);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        break;
    }
  }
}
