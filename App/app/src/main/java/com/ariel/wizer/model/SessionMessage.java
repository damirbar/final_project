package com.ariel.wizer.model;

public class SessionMessage {

    private String sid;
    private String email;
    private String type;
    private int rating;
    private int __v;
    private String _id;


    private String body[];

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String[] getBody() {
        return body;
    }

    public void setBody(String[] body) {
        this.body = body;
    }

    public String getInternal_id() {
        return sid;
    }

    public void setInternal_id(String internal_id) {
        this.sid = internal_id;
    }

}
