package com.ariel.wizer.model;


public class Session {

    private String sid;
    private String name;
    private String teacher_id;
    private String location;
    private int curr_rating;
    private String creation_date;
    private boolean hidden;
    private String videoID;
    private SessionMessage messages[];


    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
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

    public SessionMessage[] getMessages() {
        return messages;
    }

    public void setMessages(SessionMessage[] messages) {
        this.messages = messages;
    }

    public int getRating() {
        return curr_rating;
    }

    public void setRating(int rating) {
        this.curr_rating = rating;
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
}
