package com.ariel.wizeup.model;

public class Response {

    private String message;
    private String token;
    private Session session;

    public Session getSession() {
        return session;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

}
