package com.ariel.wizeup.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Session implements Parcelable {

    private String sid;
    private String name;
    private String admin;
    private String location;
    private String videoUrl;
    private String teacher_fname;
    private String teacher_lname;
    private String picID;
    private Date creation_date;
    private SessionUser students[];

    public Session(){}

    public Session(Parcel in) {
        sid = in.readString();
        name = in.readString();
        admin = in.readString();
        location = in.readString();
        videoUrl = in.readString();
        picID = in.readString();
        teacher_fname = in.readString();
        teacher_lname = in.readString();

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

    public SessionUser[] getStudents() {
        return students;
    }

    public void setStudents(SessionUser[] students) {
        this.students = students;
    }

    public Date getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Date creation_date) {
        this.creation_date = creation_date;
    }

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTeacher_fname() {
        return teacher_fname;
    }

    public void setTeacher_fname(String teacher_fname) {
        this.teacher_fname = teacher_fname;
    }

    public String getTeacher_lname() {
        return teacher_lname;
    }

    public void setTeacher_lname(String teacher_lname) {
        this.teacher_lname = teacher_lname;
    }

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
        parcel.writeString(location);
        parcel.writeString(videoUrl);
        parcel.writeString(picID);
        parcel.writeString(teacher_fname);
        parcel.writeString(teacher_lname);

    }
}
