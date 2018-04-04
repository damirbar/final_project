package com.ariel.wizer.model;


public class User {

    private String first_name;
    private String last_name;
    private String mail;
    private String password;
    private String register_date;
    private String newPassword;
    private String token;

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
        return mail;
    }

    public void setEmail(String email) {
        this.mail = email;
    }

    public String getRegisterDate() {
        return register_date;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
