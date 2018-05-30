package com.ariel.wizer.model;

public class SessionMessage  {

    private String sid;
    private String email;
    private int likes;
    private int dislikes;

    private String likers[];
    private String dislikers[];

    private String body[];
    private String type;
    private String _id;
    private String date;

    public String[] getLikers() {
        return likers;
    }

    public String[] getDislikers() {
        return dislikers;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getBody() {
        return body;
    }

    public void setBody(String[] body) {
        this.body = body;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
