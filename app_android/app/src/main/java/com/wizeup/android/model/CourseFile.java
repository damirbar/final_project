package com.wizeup.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CourseFile implements Parcelable {

    private String _id;
    private String url;
    private Date creation_date;
    private String originalName;
    private String size;
    private String publicid;
    private String uploaderid;

    public CourseFile(){}

    protected CourseFile(Parcel in) {
        _id = in.readString();
        url = in.readString();
        originalName = in.readString();
        size = in.readString();
        publicid = in.readString();
        uploaderid = in.readString();
    }

    public static final Creator<CourseFile> CREATOR = new Creator<CourseFile>() {
        @Override
        public CourseFile createFromParcel(Parcel in) {
            return new CourseFile(in);
        }

        @Override
        public CourseFile[] newArray(int size) {
            return new CourseFile[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getPublicid() {
        return publicid;
    }

    public void setPublicid(String publicid) {
        this.publicid = publicid;
    }

    public String getUploaderid() {
        return uploaderid;
    }

    public void setUploaderid(String uploaderid) {
        this.uploaderid = uploaderid;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return originalName;
    }

    public void setName(String name) {
        this.originalName = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(url);
        parcel.writeString(originalName);
        parcel.writeString(size);
        parcel.writeString(publicid);
        parcel.writeString(uploaderid);
    }
}
