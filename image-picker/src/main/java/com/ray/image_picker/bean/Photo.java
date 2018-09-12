package com.ray.image_picker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/***
  *  Author : yurui@palmax.cn
  *  Create at 2018/9/12 17:38
  *  description : 
  */
public class Photo implements Parcelable {
  public String path;
  private long dataAdded;
  private long dataModified;

  public Photo(){

  }

  public Photo(String path, long dataAdded, long dataModified) {
    this.path = path;
    this.dataAdded = dataAdded;
    this.dataModified = dataModified;
  }

  protected Photo(Parcel in) {
    path = in.readString();
    dataAdded = in.readLong();
    dataModified = in.readLong();
  }

  public static final Creator<Photo> CREATOR = new Creator<Photo>() {
    @Override
    public Photo createFromParcel(Parcel in) {
      return new Photo(in);
    }

    @Override
    public Photo[] newArray(int size) {
      return new Photo[size];
    }
  };

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public long getDataAdded() {
    return dataAdded;
  }

  public void setDataAdded(long dataAdded) {
    this.dataAdded = dataAdded;
  }

  public long getDataModified() {
    return dataModified;
  }

  public void setDataModified(long dataModified) {
    this.dataModified = dataModified;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(path);
    dest.writeLong(dataAdded);
    dest.writeLong(dataModified);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Photo photo = (Photo) o;

    if (dataAdded != photo.dataAdded) return false;
    if (dataModified != photo.dataModified) return false;
    return path != null ? path.equals(photo.path) : photo.path == null;
  }

  @Override
  public int hashCode() {
    int result = path != null ? path.hashCode() : 0;
    result = 31 * result + (int) (dataAdded ^ (dataAdded >>> 32));
    result = 31 * result + (int) (dataModified ^ (dataModified >>> 32));
    return result;
  }
}
