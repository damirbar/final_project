package com.ariel.wizer.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Session implements Parcelable {

    private String sid;
    private String name;
    private String admin;
    private String teacher_id;
    private String location;
    private String videoID;
//    private SessionMessage messages[];
    private String picID;
    //private int curr_rating;
    //private String creation_date;

    public Session(){}

    public Session(Parcel in) {
        sid = in.readString();
        name = in.readString();
        admin = in.readString();
        teacher_id = in.readString();
        location = in.readString();
        videoID = in.readString();
        picID = in.readString();
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPicID() {
        return picID;
    }

    public void setPicID(String picID) {
        this.picID = picID;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

//    public SessionMessage[] getMessages() {
//        return messages;
//    }
//
//    public void setMessages(SessionMessage[] messages) {
//        this.messages = messages;
//    }


    public String getSid() {
        return sid;
    }

    public void setSid(String internal_id) {
        this.sid = internal_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sid);
        parcel.writeString(name);
        parcel.writeString(admin);
        parcel.writeString(teacher_id);
        parcel.writeString(location);
        parcel.writeString(videoID);
        parcel.writeString(picID);
    }
}
