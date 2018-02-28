package com.perpule.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.perpule.data.RootObject;
import com.perpule.data.Song;
import com.perpule.engine.DataDownloader;

/**
 * Created by mani on 28/02/18.
 */

public class AppViewModel extends AndroidViewModel {

  private MutableLiveData<RootObject> rootObjectMutableLiveData;

  private MutableLiveData<Song> playEvent;

  private MutableLiveData<Void> continueEvent;

  public AppViewModel(@NonNull Application application) {
    super(application);

    rootObjectMutableLiveData = new MutableLiveData<>();
    playEvent = new MutableLiveData<>();
    continueEvent = new MutableLiveData<>();

  }

  public void downloadData() {
    DataDownloader dataDownloader = DataDownloader.getInstance();
    dataDownloader.getData(rootObjectMutableLiveData);
  }

  public LiveData<RootObject> getRootObject() {
    return rootObjectMutableLiveData;
  }

  public void setPlayEvent(Song song) {
    playEvent.setValue(song);
  }

  public LiveData<Song> getPlayEvent() {
    return playEvent;
  }

  public void setContinueEvent() {
    continueEvent.setValue(null);
  }

  public LiveData<Void> getContinueEvent() {
    return continueEvent;
  }
}
