package com.ariel.wizer.model;


public class Session {

    private String sid;
    private String name;
    private String teacher_id;
    private String location;
    private int rating;
    private SessionMessage messages[];

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
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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
