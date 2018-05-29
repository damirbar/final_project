package com.ariel.wizer.model;


public class Session {

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
}
