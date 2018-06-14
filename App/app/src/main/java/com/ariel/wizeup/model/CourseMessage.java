package com.ariel.wizeup.model;

import java.util.Date;

public class CourseMessage {

    private String email;
    private String poster_id;
    private String cid;
    private String type;
    private String body;
    private String name;
    private Date date;
    private CourseMessage replies[];


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public CourseMessage[] getReplies() {
        return replies;
    }

    public void setReplies(CourseMessage[] replies) {
        this.replies = replies;
    }
}
