package com.ariel.wizer.model;


public class User {

    private String display_name;
    private String mail;
    private String password;
    private String register_date;
    private String newPassword;
    private String token;

    public void setDisplayName(String name) {
        this.display_name = name;
    }

    public void setMail(String email) {
        this.mail = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getMail() {
        return mail;
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
