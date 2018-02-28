package com.perpule.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class RootObject {

  @SerializedName("data") @Expose private List<Song> data = null;

  public List<Song> getData() {
    return data;
  }

  public void setData(List<Song> data) {
    this.data = data;
  }

}