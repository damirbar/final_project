package com.wizeup.android.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {
    private String _id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String password_cnfrm;
    private String role;
    private String accessToken;
    private String display_name;
    private String about_me;
    private String country;
    private String address;
    private Date birthday;
    private String gender;
//    private String photos[];
    private String profile_img;
//    private int cred;
//    private int fame;
//    private String register_date;
//    private String last_modified;
    private String temp_password;
    private String temp_password_time;
//    private String courses[];

    public User(){}

    protected User(Parcel in) {
        _id = in.readString();
        first_name = in.readString();
        last_name = in.readString();
        email = in.readString();
        password = in.readString();
        password_cnfrm = in.readString();
        role = in.readString();
        accessToken = in.readString();
        display_name = in.readString();
        about_me = in.readString();
        country = in.readString();
        address = in.readString();
        gender = in.readString();
        profile_img = in.readString();
        temp_password = in.readString();
        temp_password_time = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public String getPassword_cnfrm() {
        return password_cnfrm;
    }

    public void setPassword_cnfrm(String password_cnfrm) {
        this.password_cnfrm = password_cnfrm;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getId_num() {
        return _id;
}

    public void setId_num(String id_num) {
        this._id = id_num;
    }

    public String getToken() {
        return accessToken;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getTemp_password() {
        return temp_password;
    }

    public void setTemp_password(String temp_password) {
        this.temp_password = temp_password;
    }

    public String getTemp_password_time() {
        return temp_password_time;
    }

    public void setTemp_password_time(String temp_password_time) {
        this.temp_password_time = temp_password_time;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFname() {
        return first_name;
    }

    public void setFname(String fname) {
        this.first_name = fname;
    }

    public String getLname() {
        return last_name;
    }

    public void setLname(String lname) {
        this.last_name = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNewPassword(String newPassword) {
        this.temp_password = newPassword;
    }

    public void setToken(String token) {
        this.accessToken = token;
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof User)) {
            return false;
        }
        User u = (User) other;
        return this.getFname().equals(u.getFname()) &&
                this.getLname().equals(u.getLname()) &&
                this.getDisplay_name().equals(u.getDisplay_name()) &&
                this.getCountry().equals(u.getCountry()) &&
                this.getAddress().equals(u.getAddress()) &&
                this.getBirthday().equals(u.getBirthday()) &&
                this.getGender().equals(u.getGender()) &&
                this.getAbout_me().equals(u.getAbout_me());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(first_name);
        parcel.writeString(last_name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(password_cnfrm);
        parcel.writeString(role);
        parcel.writeString(accessToken);
        parcel.writeString(display_name);
        parcel.writeString(about_me);
        parcel.writeString(country);
        parcel.writeString(address);
        parcel.writeString(gender);
        parcel.writeString(profile_img);
        parcel.writeString(temp_password);
        parcel.writeString(temp_password_time);
    }
}
