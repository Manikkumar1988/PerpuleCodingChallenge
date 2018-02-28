package com.perpule.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by mani on 16/12/17.
 */

/*
@Entity(tableName = TABLE_NAME)
*/
public class Song implements Serializable {

  //@PrimaryKey
  @SerializedName("itemId")
  @Expose
  private String itemId;
  @SerializedName("desc")
  @Expose
  private String desc;
  @SerializedName("audio")
  @Expose
  private String audio;

  public String getItemId() {
    return itemId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getAudio() {
    return audio;
  }

  public void setAudio(String audio) {
    this.audio = audio;
  }
}
