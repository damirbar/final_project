package com.ariel.wizeup.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class SessionMessage implements Parcelable {

    private String sid;
    private String nickname;
    private int likes;
    private int dislikes;
    private String likers[];
    private String dislikers[];
    private String body;
    private String type;
    private String _id;
    private Date date;
    private String mid;
    private String poster_id;
    private String replies[];
    private String replier_id;


    public SessionMessage() { }

    protected SessionMessage(Parcel in) {
        sid = in.readString();
        nickname = in.readString();
        likes = in.readInt();
        dislikes = in.readInt();
        likers = in.createStringArray();
        dislikers = in.createStringArray();
        body = in.readString();
        type = in.readString();
        _id = in.readString();
        mid = in.readString();
        poster_id = in.readString();
        replies = in.createStringArray();
        replier_id = in.readString();
    }

    public static final Creator<SessionMessage> CREATOR = new Creator<SessionMessage>() {
        @Override
        public SessionMessage createFromParcel(Parcel in) {
            return new SessionMessage(in);
        }

        @Override
        public SessionMessage[] newArray(int size) {
            return new SessionMessage[size];
        }
    };

    public String getReplier_id() {
        return replier_id;
    }

    public void setReplier_id(String replier_id) {
        this.replier_id = replier_id;
    }

    public String[] getReplies() {
        return replies;
    }

    public void setReplies(String[] replies) {
        this.replies = replies;
    }

    public String getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(String poster_id) {
        this.poster_id = poster_id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setLikers(String[] likers) {
        this.likers = likers;
    }

    public void setDislikers(String[] dislikers) {
        this.dislikers = dislikers;
    }

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(sid);
        parcel.writeString(nickname);
        parcel.writeInt(likes);
        parcel.writeInt(dislikes);
        parcel.writeStringArray(likers);
        parcel.writeStringArray(dislikers);
        parcel.writeString(body);
        parcel.writeString(type);
        parcel.writeString(_id);
        parcel.writeString(mid);
        parcel.writeString(poster_id);
        parcel.writeStringArray(replies);
        parcel.writeString(replier_id);
    }
}
