package com.perpule.engine;

import android.arch.lifecycle.MutableLiveData;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.perpule.BuildConfig;
import com.perpule.data.RootObject;
import com.perpule.data.Song;
import com.perpule.remote.PerpuleApi;
import com.perpule.util.Util;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mani on 28/02/18.
 */

public class DataDownloader {

  private static DataDownloader sDataDownloader;

  public static DataDownloader getInstance() {
    if(sDataDownloader == null) {
      sDataDownloader = new DataDownloader();
    }
    return sDataDownloader;
  }

  private PerpuleApi gerritAPI;

  private DataDownloader() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.ENDPOINT)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();

    gerritAPI = retrofit.create(PerpuleApi.class);
  }

  public void getData(final MutableLiveData<RootObject> rootObjectMutableLiveData) {
    Call<RootObject> call = gerritAPI.getFeed();
    call.enqueue(new Callback<RootObject>() {
      @Override public void onResponse(Call<RootObject> call, Response<RootObject> response) {
        if(response.isSuccessful()) {
          rootObjectMutableLiveData.setValue(response.body());
          List<Song> changesList = response.body().getData();
          for(Song change:changesList) {
            Util.Logger(change.getDesc());
          }
        } else {
          Util.Logger(response.message());
        }
      }

      @Override public void onFailure(Call<RootObject> call, Throwable t) {
        Util.Logger("Error Occurred");
      }
    });
  }
}
