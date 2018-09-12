package com.ray.image_picker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/***
  *  Author : yurui@palmax.cn
  *  Create at 2018/9/12 17:47
  *  description : 
  */
public class Album implements Parcelable {
  private String name;
  private String id;
  private String coverPath;

  public Album() {
  }

  protected Album(Parcel in) {
    name = in.readString();
    id = in.readString();
    coverPath = in.readString();
  }

  public static final Creator<Album> CREATOR = new Creator<Album>() {
    @Override
    public Album createFromParcel(Parcel in) {
      return new Album(in);
    }

    @Override
    public Album[] newArray(int size) {
      return new Album[size];
    }
  };

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCoverPath() {
    return coverPath;
  }

  public void setCoverPath(String coverPath) {
    this.coverPath = coverPath;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(id);
    dest.writeString(coverPath);
  }
}
