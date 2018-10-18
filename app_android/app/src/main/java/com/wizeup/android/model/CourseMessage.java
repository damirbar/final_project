package com.wizeup.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class CourseMessage implements Parcelable {

    private String email;
    private String poster_id;
    private String cid;
    private String type;
    private String body;
    private String name;
    private Date date;
    private String _id;
    private String mid;
    private String replies[];
    private String replier_id;
    private String image;


    protected CourseMessage(Parcel in) {
        email = in.readString();
        poster_id = in.readString();
        cid = in.readString();
        type = in.readString();
        body = in.readString();
        name = in.readString();
        _id = in.readString();
        mid = in.readString();
        replies = in.createStringArray();
        replier_id = in.readString();
        image = in.readString();
    }

    public static final Creator<CourseMessage> CREATOR = new Creator<CourseMessage>() {
        @Override
        public CourseMessage createFromParcel(Parcel in) {
            return new CourseMessage(in);
        }

        @Override
        public CourseMessage[] newArray(int size) {
            return new CourseMessage[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReplier_id() {
        return replier_id;
    }

    public void setReplier_id(String replier_id) {
        this.replier_id = replier_id;
    }

    public CourseMessage() { }


    public String[] getReplies() {
        return replies;
    }

    public void setReplies(String[] replies) {
        this.replies = replies;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(poster_id);
        parcel.writeString(cid);
        parcel.writeString(type);
        parcel.writeString(body);
        parcel.writeString(name);
        parcel.writeString(_id);
        parcel.writeString(mid);
        parcel.writeStringArray(replies);
        parcel.writeString(replier_id);
        parcel.writeString(image);
    }
}
