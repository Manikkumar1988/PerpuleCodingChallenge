package com.perpule.ui.home;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.perpule.R;
import com.perpule.data.RootObject;
import com.perpule.data.Song;
import com.perpule.downloadmanager.DownloadResultReceiver;
import com.perpule.downloadmanager.DownloadService;
import com.perpule.player.PlayerService;
import com.perpule.ui.AppViewModel;
import com.perpule.ui.splash.SplashActivity;
import com.perpule.util.Util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemFragment extends LifecycleFragment implements View.OnClickListener, DownloadResultReceiver.Receiver{

  public static final String FRAGMENT_TAG = "item_fragment";

  @BindView(R.id.continueSong) Button continueSong;
  @BindView(R.id.item_name) TextView textView;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootLayout = inflater.inflate(R.layout.fragment_item, container, false);
    ButterKnife.bind(this,rootLayout);

    initializeView();

    return rootLayout;
  }

  private void initializeView() {

    continueSong.setOnClickListener(this);
    continueSong.setVisibility(View.GONE);

    Song song = (Song) this.getArguments().getSerializable("CurrentObject");
    Song nextSong = (Song) this.getArguments().getSerializable("NextObject");

    textView.setText(song.getDesc());


   appViewModel.setPlayEvent(song);

    /* Hide progress & extract result from bundle */
    String url = nextSong.getAudio();

    DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
    mReceiver.setReceiver(this);
    Intent intent = new Intent(getActivity(), DownloadService.class);

        /* Send optional extras to Download IntentService */
    intent.putExtra("url", url);
    intent.putExtra("receiver", mReceiver);

    getActivity().startService(intent);
  }

  @Override public void onClick(View view) {
    switch (view.getId()) {
      case R.id.continueSong:{
        appViewModel.setContinueEvent();
        break;
      }
    }
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    initializeViewModel();
  }

  AppViewModel appViewModel;
  public void initializeViewModel() {
    appViewModel =
        ViewModelProviders.of(getActivity()).get(AppViewModel.class);
  }


  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {
    switch (resultCode) {
      case DownloadService.STATUS_RUNNING:
        //TODO: Indicate in UI
        break;
      case DownloadService.STATUS_FINISHED:
        continueSong.setVisibility(View.VISIBLE);
        break;
      case DownloadService.STATUS_ERROR:
                /* Handle the error */
        String error = resultData.getString(Intent.EXTRA_TEXT);
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        break;
    }
  }

}
